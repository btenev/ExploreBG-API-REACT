package bg.exploreBG.service.moderation.common;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.EntityIdsToDeleteDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailImageStatusAndGpxFileStatus;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.entity.*;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.querybuilder.ImageQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.reviewable.ReviewableEntity;
import bg.exploreBG.reviewable.ReviewableWithImages;
import bg.exploreBG.service.EntityUpdateService;
import bg.exploreBG.service.GenericPersistenceService;
import bg.exploreBG.service.ImageService;
import bg.exploreBG.updatable.UpdatableEntity;
import bg.exploreBG.updatable.UpdatableEntityDto;
import bg.exploreBG.utils.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class ReviewService {
    private final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final EntityUpdateService entityUpdateService;
    private final ImageClaimService imageClaimService;
    private final ImageApprovalService imageApprovalService;
    private final ImageService imageService;
    private final EntityClaimService entityClaimService;
    private final GenericPersistenceService<ImageEntity> imagePersistence;
    private final GenericPersistenceService<GpxEntity> gpxPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final ImageQueryBuilder imageQueryBuilder;

    public ReviewService(
            EntityUpdateService entityUpdateService,
            ImageClaimService imageClaimService,
            ImageApprovalService imageApprovalService,
            ImageService imageService,
            EntityClaimService entityClaimService,
            GenericPersistenceService<ImageEntity> imagePersistence,
            GenericPersistenceService<GpxEntity> gpxPersistence,
            UserQueryBuilder userQueryBuilder,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            ImageQueryBuilder imageQueryBuilder
    ) {
        this.entityUpdateService = entityUpdateService;
        this.imageClaimService = imageClaimService;
        this.imageApprovalService = imageApprovalService;
        this.imageService = imageService;
        this.entityClaimService = entityClaimService;
        this.imagePersistence = imagePersistence;
        this.gpxPersistence = gpxPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.imageQueryBuilder = imageQueryBuilder;
    }

    public <E extends ReviewableEntity> void toggleClaim(
            Long entityId,
            Boolean claimEntity,
            UserDetails userDetails,
            Function<Long, E> entityFetcher,
            Consumer<E> entitySaver
    ) {
        E entity = entityFetcher.apply(entityId);

        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        this.entityClaimService.toggleEntityClaim(entity, claimEntity, reviewer);

        entitySaver.accept(entity);
    }

    public void toggleGpxFileClaim(
            GpxEntity gpxFile,
            Boolean claimGpx,
            UserDetails userDetails
    ) {
        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        this.entityClaimService.toggleEntityClaim(gpxFile, claimGpx, reviewer);

        this.gpxPersistence.saveEntityWithoutReturn(gpxFile);
    }

    public <T extends ReviewableEntity & UpdatableEntity> T validateAndApproveEntity(
            Long entityId,
            Function<Long, T> entityFetcher,
            UpdatableEntityDto<T> dto,
            ExploreBgUserDetails reviewer
    ) {
        T entity = entityFetcher.apply(entityId);

        validateItemApproval(entity, reviewer);

        if (dto != null) {
            this.entityUpdateService.updateFieldsIfNecessary(entity, dto);
        }

        entity.setStatus(StatusEnum.APPROVED);
        return entity;
    }

    public <T extends ReviewableWithImages & UpdatableEntity> SuperUserReviewStatusEnum approveEntity(
            Long entityId,
            Function<Long, T> entityFetcher,
            UpdatableEntityDto<T> dto,
            ExploreBgUserDetails userDetails,
            Function<T, T> saveEntityWithReturn
    ) {
        T entity = validateAndApproveEntity(
                entityId,
                entityFetcher,
                dto,
                userDetails);

        if (entity instanceof HikingTrailEntity trail) {
            HikingTrailImageStatusAndGpxFileStatus statuses =
                    this.hikingTrailQueryBuilder.getHikingTrailImageStatusAndGpxStatusById(entityId);
            logger.info("Hiking trail image and gpx status: {}", statuses);
            updateTrailStatusIfEligible(statuses, trail);
        } else if (entity instanceof AccommodationEntity accommodation) {
            updateAccommodationStatusIfEligible(entityId, accommodation);
        } else if (entity instanceof DestinationEntity destination) {
            updateDestinationStatusIfEligible(entityId, destination);
        }

        entity = saveEntityWithReturn.apply(entity);

        return entity.getEntityStatus();
    }

    public <T extends ReviewableWithImages> SuperUserReviewStatusEnum approveEntityImages(
            Long entityId,
            Function<Long, T> entityFetcher,
            ImageApproveDto imageApprove,
            UserDetails userDetails,
            String folder,
            Function<T, T> saveEntityWithReturn
    ) {
        T entity = saveApprovedImages(
                entityId,
                entityFetcher,
                imageApprove,
                userDetails);

        List<Long> approvedIds = ImageUtils.filterApprovedImageIds(entity.getImages(), userDetails.getUsername());

        if (!approvedIds.isEmpty()) {
            entity = this.imageService
                    .deleteImagesFromEntityWithReturn(
                            entity, new EntityIdsToDeleteDto(folder, approvedIds), saveEntityWithReturn);
        }

        boolean isTrailApproved = true;
        if (entity instanceof HikingTrailEntity trail) {
            isTrailApproved = trail.getGpxFile() == null || trail.getGpxFile().getStatus() == StatusEnum.APPROVED;
        }

        if (isTrailApproved
                && entity.getStatus() == StatusEnum.APPROVED
                && ImageUtils.countNotApprovedImages(entity.getImages()) == 0) {
            entity.setEntityStatus(SuperUserReviewStatusEnum.APPROVED);
            entity = saveEntityWithReturn.apply(entity);
        }

        return entity.getEntityStatus();
    }

    public <T extends ReviewableWithImages> void toggleImageClaimAndSave(
            Long entityId,
            Function<Long, T> entityFetcher,
            Boolean claimImage,
            UserDetails userDetails
    ) {
        T entity = entityFetcher.apply(entityId);

        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        List<ImageEntity> claimed = this.imageClaimService.toggleImageClaim(entity, claimImage, reviewer);

        this.imagePersistence.saveEntitiesWithoutReturn(claimed);
    }

    private <T extends ReviewableWithImages> T saveApprovedImages(
            Long entityId,
            Function<Long, T> entityFetcher,
            ImageApproveDto approveDto,
            UserDetails userDetails
    ) {
        T entity = entityFetcher.apply(entityId);

        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        List<ImageEntity> approved =
                this.imageApprovalService.approvalWithValidation(entity, approveDto.imageIds(), reviewer);

        this.imagePersistence.saveEntitiesWithoutReturn(approved);

        return entity;
    }

    public <T extends ReviewableEntity> void validateItemApproval(
            T item,
            ExploreBgUserDetails userDetails
    ) {
        StatusEnum status = item.getStatus();
        String reviewedByUserProfile =
                Optional.ofNullable(item.getReviewedBy())
                        .map(UserEntity::getUsername)
                        .orElse(null);

        if (reviewedByUserProfile == null) {
            throw new AppException("A pending item cannot be approved!", HttpStatus.BAD_REQUEST);
        }

        if (status == StatusEnum.REVIEW && !reviewedByUserProfile.equals(userDetails.getProfileName())) {
            throw new AppException("The item has already been claimed by another user! You can not approve it!", HttpStatus.BAD_REQUEST);
        }

        validateApprovedStatus(status);
    }

    private void validateApprovedStatus(StatusEnum entityStatus) {
        if (entityStatus == StatusEnum.APPROVED) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }
    }

    private static void updateTrailStatusIfEligible(
            HikingTrailImageStatusAndGpxFileStatus statuses,
            HikingTrailEntity currentTrail
    ) {
        if ((statuses.imageStatus() == null || !statuses.imageStatus().equals("NOT_APPROVED")
                && StatusEnum.valueOf(statuses.imageStatus()) == StatusEnum.APPROVED)
                && statuses.gpxFileStatus() == null || statuses.gpxFileStatus() == StatusEnum.APPROVED) {
            currentTrail.setEntityStatus(SuperUserReviewStatusEnum.APPROVED);
        }
    }

    private void updateAccommodationStatusIfEligible(Long accommodationId, AccommodationEntity accommodation) {
        if (this.imageQueryBuilder.getCountOfApprovedImagesByAccommodationId(accommodationId) == 0) {
            accommodation.setEntityStatus(SuperUserReviewStatusEnum.APPROVED);
        }
    }

    private void updateDestinationStatusIfEligible(Long destinationId, DestinationEntity destination) {
        if (this.imageQueryBuilder.getCountOfApprovedImagesByDestinationId(destinationId) == 0) {
            destination.setEntityStatus(SuperUserReviewStatusEnum.APPROVED);
        }
    }
}
