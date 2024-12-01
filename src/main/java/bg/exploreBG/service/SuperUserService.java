package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.EntitiesPendingApprovalCountDto;
import bg.exploreBG.model.dto.EntityIdsToDeleteDto;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.gpxFile.validate.GpxApproveDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailImageStatusAndGpxFileStatus;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SuperUserService {
    private static final Logger logger = LoggerFactory.getLogger(SuperUserService.class);
    private final ReviewService reviewService;
    private final ImageService imageService;
    private final HikingTrailMapper hikingTrailMapper;
    private final GpxService gpxService;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;
    private final GenericPersistenceService<GpxEntity> gpxPersistence;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final UserQueryBuilder userQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;

    public SuperUserService(
            ReviewService reviewService,
            ImageService imageService,
            HikingTrailMapper hikingTrailMapper,
            GpxService gpxService,
            GenericPersistenceService<HikingTrailEntity> trailPersistence,
            GenericPersistenceService<GpxEntity> gpxPersistence,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            UserQueryBuilder userQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder
    ) {
        this.reviewService = reviewService;
        this.imageService = imageService;
        this.hikingTrailMapper = hikingTrailMapper;
        this.gpxService = gpxService;
        this.trailPersistence = trailPersistence;
        this.gpxPersistence = gpxPersistence;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.userQueryBuilder = userQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public EntitiesPendingApprovalCountDto getPendingApprovalEntitiesCount() {
        int accommodationCount =
                this.accommodationQueryBuilder
                        .getAccommodationCountByAccommodationStatus(SuperUserReviewStatusEnum.PENDING);
        int destinationCount =
                this.destinationQueryBuilder.getDestinationCountByStatus(SuperUserReviewStatusEnum.PENDING);
        int trailCount =
                this.hikingTrailQueryBuilder.getTrailCountByStatus(SuperUserReviewStatusEnum.PENDING);
        return new EntitiesPendingApprovalCountDto(accommodationCount, destinationCount, trailCount);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Page<HikingTrailForApprovalProjection> getAllHikingTrailsForApproval(
            Pageable pageable
    ) {
        return this.hikingTrailQueryBuilder.getAllHikingTrailsByStatus(SuperUserReviewStatusEnum.PENDING, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerId(Long id) {
        Long reviewerId = this.hikingTrailQueryBuilder.getReviewerId(id);
        return new UserIdDto(reviewerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleTrailClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailQueryBuilder.getHikingTrailById(trailId);
        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        this.reviewService.toggleEntityClaim(currentTrail, reviewBoolean.review(), reviewer);

        this.trailPersistence.saveEntityWithoutReturn(currentTrail);
        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleTrailGpxFileClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailWithGpxFileById(trailId);

        GpxEntity gpxFile = currentTrail.getGpxFile();
        if (gpxFile == null) {
            throw new AppException("Cannot claim a GPX file that doesn't exist!", HttpStatus.BAD_REQUEST);
        }

        UserEntity reviewer = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        this.reviewService.toggleEntityClaim(gpxFile, reviewBoolean.review(), reviewer);
        this.gpxPersistence.saveEntityWithoutReturn(gpxFile);

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public HikingTrailReviewDto reviewTrail(
            Long trailId,
            ExploreBgUserDetails userDetails,
            SuperUserReviewStatusEnum supeStatus
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailWithImagesByIdAndTrailStatus(trailId, supeStatus);

        StatusEnum status = currentTrail.getStatus();

        if (isEligibleForReview(status, currentTrail.getReviewedBy(), userDetails)) {
            return this.hikingTrailMapper.hikingTrailEntityToHikingTrailReviewDto(currentTrail);
        }

        for (ImageEntity image : currentTrail.getImages()) {
            logger.info("Image reviewer:{}", image.getReviewedBy());
            if (isEligibleForReview(image.getStatus(), image.getReviewedBy(), userDetails)) {
                return this.hikingTrailMapper.hikingTrailEntityToHikingTrailReviewDto(currentTrail);
            }
        }
        /*TODO: add gpx file validation*/
        throw new AppException("Item with invalid status for review!", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean approveTrail(
            Long trailId,
            HikingTrailCreateOrReviewDto trailCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailQueryBuilder.getHikingTrailById(trailId);
        this.reviewService.validateAndApproveEntity(currentTrail, trailCreateOrReview, userDetails);

        HikingTrailImageStatusAndGpxFileStatus statuses =
                this.hikingTrailQueryBuilder.getHikingTrailImageStatusAndGpxStatusById(trailId);
        logger.info("Hiking trail image and gpx status: {}", statuses);
        if ((statuses.imageStatus() == null || !statuses.imageStatus().equals("NOT_APPROVED") && StatusEnum.valueOf(statuses.imageStatus()) == StatusEnum.APPROVED)
                && statuses.gpxFileStatus() == null || statuses.gpxFileStatus() == StatusEnum.APPROVED) {
            currentTrail.setTrailStatus(SuperUserReviewStatusEnum.APPROVED);
        }

        this.trailPersistence.saveEntityWithoutReturn(currentTrail);

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean approveTrailGpxFile(
            Long trailId,
            GpxApproveDto gpxApprove,
            ExploreBgUserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailWithGpxFileById(trailId);

        if (currentTrail.getGpxFile() == null) {
            throw new AppException("Cannot approve a GPX file that doesn't exist!", HttpStatus.BAD_REQUEST);
        }

        this.reviewService.validateItemApproval(currentTrail.getGpxFile(), userDetails);

        if (gpxApprove.approved()) {
            currentTrail.getGpxFile().setStatus(StatusEnum.APPROVED);
            this.gpxPersistence.saveEntityWithoutReturn(currentTrail.getGpxFile());
        } else {
            this.gpxService.deleteGpxFileByTrailEntity(currentTrail);
        }

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleTrailImageClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailQueryBuilder.getHikingTrailWithImagesById(trailId);

        this.reviewService.toggleImageClaimAndSave(currentTrail, reviewBoolean, userDetails);

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean approveTrailImages(
            Long trailId,
            ImageApproveDto imageApproveDto,
            UserDetails userDetails,
            String folder
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailWithImagesAndImageReviewerAndGpxFileById(trailId);

        currentTrail = this.reviewService
                .saveApprovedImages(currentTrail, imageApproveDto, userDetails);

        List<Long> approvedIds = currentTrail.getImages().stream()
                .filter(image -> image.getStatus() == StatusEnum.REVIEW && image.getReviewedBy().getEmail().equals(userDetails.getUsername()))
                .map(ImageEntity::getId)
                .toList();

        if (!approvedIds.isEmpty()) {
            currentTrail =
                    this.imageService
                            .deleteTrailPictureByEntity(currentTrail, new EntityIdsToDeleteDto(folder, approvedIds));
        }

        if (currentTrail.getGpxFile() == null || currentTrail.getGpxFile().getStatus() == StatusEnum.APPROVED
                && currentTrail.getStatus() == StatusEnum.APPROVED) {
            currentTrail.setTrailStatus(SuperUserReviewStatusEnum.APPROVED);
        }

        this.trailPersistence.saveEntityWithoutReturn(currentTrail);

        return true;
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
