package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.reviewable.ReviewableEntity;
import bg.exploreBG.reviewable.ReviewableWithGpx;
import bg.exploreBG.reviewable.ReviewableWithImages;
import bg.exploreBG.updatable.UpdatableEntity;
import bg.exploreBG.updatable.UpdatableEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class ReviewService {
    private final Logger logger = LoggerFactory.getLogger(ReviewService.class);
    private final EntityUpdateService entityUpdateService;
    private final ImageClaimService imageClaimService;
    private final ImageApprovalService imageApprovalService;
    private final EntityClaimService entityClaimService;
    private final GenericPersistenceService<ImageEntity> imagePersistence;
    private final GenericPersistenceService<GpxEntity> gpxPersistence;
    private final UserQueryBuilder userQueryBuilder;

    public ReviewService(
            EntityUpdateService entityUpdateService,
            ImageClaimService imageClaimService,
            ImageApprovalService imageApprovalService,
            EntityClaimService entityClaimService,
            GenericPersistenceService<ImageEntity> imagePersistence,
            GenericPersistenceService<GpxEntity> gpxPersistence,
            UserQueryBuilder userQueryBuilder
    ) {
        this.entityUpdateService = entityUpdateService;
        this.imageClaimService = imageClaimService;
        this.imageApprovalService = imageApprovalService;
        this.entityClaimService = entityClaimService;
        this.imagePersistence = imagePersistence;
        this.gpxPersistence = gpxPersistence;
        this.userQueryBuilder = userQueryBuilder;
    }

    public <T extends ReviewableWithImages> Object reviewItem(
            T item,
            Function<T, Object> mapper,
            ExploreBgUserDetails userDetails
    ) {
        if (isEligibleForReview(item.getStatus(), item.getReviewedBy(), userDetails)) {
            return mapper.apply(item);
        }

        if (item.getImages() != null) {
            for (ImageEntity image : item.getImages()) {
                logger.info("Image reviewer:{}", image.getReviewedBy());
                if (isEligibleForReview(image.getStatus(), image.getReviewedBy(), userDetails)) {
                    return mapper.apply(item);
                }
            }
        }

        if (item instanceof ReviewableWithGpx) {
            GpxEntity gpxFile = ((ReviewableWithGpx) item).getGpxFile();
            if (gpxFile != null && isEligibleForReview(
                    gpxFile.getStatus(),
                    gpxFile.getReviewedBy(),
                    userDetails)) {
                return mapper.apply(item);
            }
        }

        throw new AppException("Item with invalid status for review!", HttpStatus.BAD_REQUEST);
    }

    public <E extends ReviewableEntity> void toggleClaim(
            Long entityId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails,
            Function<Long, E> entityFetcher,
            Consumer<E> entitySaver
    ) {
        E entity = entityFetcher.apply(entityId);

        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        this.entityClaimService.toggleEntityClaim(entity, reviewBoolean.review(), reviewer);

        entitySaver.accept(entity);
    }

    public <E extends ReviewableEntity> void toggleGpxFileClaim(
            GpxEntity gpxFile,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        this.entityClaimService.toggleEntityClaim(gpxFile, reviewBoolean.review(), reviewer);

        this.gpxPersistence.saveEntityWithoutReturn(gpxFile);
    }

    public <T extends ReviewableEntity & UpdatableEntity> void validateAndApproveEntity(
            T entity,
            UpdatableEntityDto<T> dto,
            ExploreBgUserDetails reviewer
    ) {
        validateItemApproval(entity, reviewer);

        if (dto != null) {
            this.entityUpdateService.updateFieldsIfNecessary(entity, dto);
        }

        entity.setStatus(StatusEnum.APPROVED);
    }

    public <T extends ReviewableWithImages> void toggleImageClaimAndSave(
            Long entityId,
            Function<Long, T> entityFetcher,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        T entity = entityFetcher.apply(entityId);

        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        List<ImageEntity> claimed = this.imageClaimService.toggleImageClaim(entity, reviewBoolean.review(), reviewer);

        this.imagePersistence.saveEntitiesWithoutReturn(claimed);
    }

    public <T extends ReviewableWithImages> T saveApprovedImages(
            T entity,
            ImageApproveDto approveDto,
            UserDetails userDetails
    ) {
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

    private boolean isEligibleForReview(
            StatusEnum status,
            UserEntity reviewedBy,
            ExploreBgUserDetails userDetails
    ) {
        if (status == StatusEnum.PENDING) {
            return true;
        }

        return status == StatusEnum.REVIEW &&
                Objects.equals(reviewedBy != null
                                ? reviewedBy.getUsername() : null,
                        userDetails.getProfileName());
    }
}
