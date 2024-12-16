package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.EntitiesPendingApprovalCountDto;
import bg.exploreBG.model.dto.EntityIdsToDeleteDto;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationForApprovalProjection;
import bg.exploreBG.model.dto.accommodation.AccommodationReviewDto;
import bg.exploreBG.model.dto.gpxFile.validate.GpxApproveDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailImageStatusAndGpxFileStatus;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.querybuilder.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuperUserService {
    private static final Logger logger = LoggerFactory.getLogger(SuperUserService.class);
    private final ReviewService reviewService;
    private final ImageService imageService;
    private final HikingTrailMapper hikingTrailMapper;
    private final AccommodationMapper accommodationMapper;
    private final GpxService gpxService;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;
    private final GenericPersistenceService<GpxEntity> gpxPersistence;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final UserQueryBuilder userQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final ImageQueryBuilder imageQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final GpxQueryBuilder gpxQueryBuilder;

    public SuperUserService(
            ReviewService reviewService,
            ImageService imageService,
            HikingTrailMapper hikingTrailMapper,
            AccommodationMapper accommodationMapper,
            GpxService gpxService,
            GenericPersistenceService<HikingTrailEntity> trailPersistence,
            GenericPersistenceService<GpxEntity> gpxPersistence,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            UserQueryBuilder userQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            ImageQueryBuilder imageQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder,
            GpxQueryBuilder gpxQueryBuilder
    ) {
        this.reviewService = reviewService;
        this.imageService = imageService;
        this.hikingTrailMapper = hikingTrailMapper;
        this.accommodationMapper = accommodationMapper;
        this.gpxService = gpxService;
        this.trailPersistence = trailPersistence;
        this.gpxPersistence = gpxPersistence;
        this.accommodationPersistence = accommodationPersistence;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.userQueryBuilder = userQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.imageQueryBuilder = imageQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.gpxQueryBuilder = gpxQueryBuilder;
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
        return this.hikingTrailQueryBuilder.getAllHikingTrailsByTrailStatus(SuperUserReviewStatusEnum.PENDING, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Page<AccommodationForApprovalProjection> getAllAccommodationForApproval(
            Pageable pageable
    ) {
        return this.accommodationQueryBuilder
                .getAllAccommodationsByAccommodationStatus(SuperUserReviewStatusEnum.PENDING, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerIdByTrailId(Long trailId) {
        Long reviewerId = this.hikingTrailQueryBuilder.getReviewerId(trailId);
        return new UserIdDto(reviewerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerIdByImageId(Long imageId) {
        Long reviewerId = this.imageQueryBuilder.getReviewerIdByImageId(imageId);
        return new UserIdDto(reviewerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerIdByGpxId(Long gpxId) {
        Long reviewerId = this.gpxQueryBuilder.getReviewerIdByGpxId(gpxId);
        return new UserIdDto(reviewerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleTrailClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        this.reviewService
                .toggleClaim(
                        trailId,
                        reviewBoolean,
                        userDetails,
                        this.hikingTrailQueryBuilder::getHikingTrailById,
                        this.trailPersistence::saveEntityWithoutReturn);

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleAccommodationClaim(
            Long accommodationId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        this.reviewService
                .toggleClaim(
                        accommodationId,
                        reviewBoolean,
                        userDetails,
                        this.accommodationQueryBuilder::getAccommodationEntityById,
                        this.accommodationPersistence::saveEntityWithoutReturn);

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

        this.reviewService.toggleGpxFileClaim(gpxFile, reviewBoolean, userDetails);
        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public HikingTrailReviewDto reviewTrail(
            Long trailId,
            ExploreBgUserDetails userDetails,
            SuperUserReviewStatusEnum supeStatus
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndTrailStatus(trailId, supeStatus);

        return (HikingTrailReviewDto) this.reviewService
                .reviewItem(
                        currentTrail,
                        this.hikingTrailMapper::hikingTrailEntityToHikingTrailReviewDto,
                        userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public AccommodationReviewDto reviewAccommodation(
            Long accommodationId,
            ExploreBgUserDetails userDetails,
            SuperUserReviewStatusEnum supeStatus
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndAccommodationStatus(accommodationId, supeStatus);

        return (AccommodationReviewDto) this.reviewService
                .reviewItem(
                        current,
                        accommodationMapper::accommodationEntityToAccommodationReviewDto,
                        userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveTrail(
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

        HikingTrailEntity saved = this.trailPersistence.saveEntityWithReturn(currentTrail);

        return saved.getTrailStatus();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveTrailGpxFile(
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

        if (currentTrail.getStatus() == StatusEnum.APPROVED
                && this.imageQueryBuilder.getCountOfNonApprovedImagesByTrailId(trailId) == 0) {
            currentTrail.setTrailStatus(SuperUserReviewStatusEnum.APPROVED);
            currentTrail = this.trailPersistence.saveEntityWithReturn(currentTrail);
        }

        return currentTrail.getTrailStatus();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleTrailImageClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                trailId,
                this.hikingTrailQueryBuilder::getHikingTrailWithImagesAndImageReviewerById,
                reviewBoolean,
                userDetails
        );

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleAccommodationImageClaim(
            Long accommodationId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                accommodationId,
                this.accommodationQueryBuilder::getAccommodationWithImagesAndImageReviewerById,
                reviewBoolean,
                userDetails
        );

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveTrailImages(
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

        HikingTrailEntity saved = this.trailPersistence.saveEntityWithReturn(currentTrail);

        return saved.getTrailStatus();
    }
}
