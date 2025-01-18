package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.EntitiesPendingApprovalCountDto;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationForApprovalProjection;
import bg.exploreBG.model.dto.accommodation.AccommodationReviewDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateOrReviewDto;
import bg.exploreBG.model.dto.destination.DestinationForApprovalProjection;
import bg.exploreBG.model.dto.destination.DestinationReviewDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.dto.gpxFile.validate.GpxApproveDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.model.mapper.DestinationMapper;
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

@Service
public class SuperUserService {
    private static final Logger logger = LoggerFactory.getLogger(SuperUserService.class);
    private final ReviewService reviewService;
    private final HikingTrailMapper hikingTrailMapper;
    private final AccommodationMapper accommodationMapper;
    private final GpxService gpxService;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;
    private final GenericPersistenceService<GpxEntity> gpxPersistence;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final ImageQueryBuilder imageQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final GpxQueryBuilder gpxQueryBuilder;
    private final DestinationMapper destinationMapper;

    public SuperUserService(
            ReviewService reviewService,
            HikingTrailMapper hikingTrailMapper,
            AccommodationMapper accommodationMapper,
            GpxService gpxService,
            GenericPersistenceService<HikingTrailEntity> trailPersistence,
            GenericPersistenceService<GpxEntity> gpxPersistence,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            ImageQueryBuilder imageQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder,
            GpxQueryBuilder gpxQueryBuilder,
            DestinationMapper destinationMapper) {
        this.reviewService = reviewService;
        this.hikingTrailMapper = hikingTrailMapper;
        this.accommodationMapper = accommodationMapper;
        this.gpxService = gpxService;
        this.trailPersistence = trailPersistence;
        this.gpxPersistence = gpxPersistence;
        this.accommodationPersistence = accommodationPersistence;
        this.destinationPersistence = destinationPersistence;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.imageQueryBuilder = imageQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.gpxQueryBuilder = gpxQueryBuilder;
        this.destinationMapper = destinationMapper;
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
    public boolean toggleDestinationClaim(
            Long destinationId,
            ReviewBooleanDto reviewBooleanDto,
            UserDetails userDetails
    ) {
        this.reviewService
                .toggleClaim(
                        destinationId,
                        reviewBooleanDto,
                        userDetails,
                        this.destinationQueryBuilder::getDestinationEntityById,
                        this.destinationPersistence::saveEntityWithoutReturn);

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
    public DestinationReviewDto reviewDestination(
            Long destinationId,
            ExploreBgUserDetails userDetails,
            SuperUserReviewStatusEnum supeStatus
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndDestinationStatus(destinationId, supeStatus);

        return (DestinationReviewDto) this.reviewService
                .reviewItem(
                        current,
                        destinationMapper::destinationEntityToDestinationReviewDto,
                        userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveTrail(
            Long trailId,
            HikingTrailCreateOrReviewDto trailCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        return this.reviewService.approveEntity(
                trailId,
                this.hikingTrailQueryBuilder::getHikingTrailById,
                trailCreateOrReview,
                userDetails,
                this.trailPersistence::saveEntityWithReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveAccommodation(
            Long accommodationId,
            AccommodationCreateOrReviewDto accommodationCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        return this.reviewService.approveEntity(
                accommodationId,
                this.accommodationQueryBuilder::getAccommodationEntityById,
                accommodationCreateOrReview,
                userDetails,
                this.accommodationPersistence::saveEntityWithReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveDestination(
            Long destinationId,
            DestinationCreateOrReviewDto destinationCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        return this.reviewService.approveEntity(
                destinationId,
                this.destinationQueryBuilder::getDestinationEntityById,
                destinationCreateOrReview,
                userDetails,
                this.destinationPersistence::saveEntityWithReturn);
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

        currentTrail = updateTrailStatusToApprovedIfEligibleAndReturnTrail(trailId, currentTrail);

        return currentTrail.getEntityStatus();
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
    public boolean toggleDestinationImageClaim(
            Long destinationId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                destinationId,
                this.destinationQueryBuilder::getDestinationWithImagesAndImageReviewerById,
                reviewBoolean,
                userDetails
        );

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveTrailImages(
            Long trailId,
            ImageApproveDto imageApprove,
            UserDetails userDetails,
            String folder
    ) {
        return this.reviewService.approveEntityImages(
                trailId,
                this.hikingTrailQueryBuilder::getHikingTrailWithImagesAndImageReviewerById,
                imageApprove,
                userDetails,
                folder,
                this.trailPersistence::saveEntityWithReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveAccommodationImages(
            Long accommodationId,
            ImageApproveDto imageApprove,
            UserDetails userDetails,
            String folder
    ) {
        return this.reviewService.approveEntityImages(
                accommodationId,
                this.accommodationQueryBuilder::getAccommodationWithImagesAndImageReviewerById,
                imageApprove,
                userDetails,
                folder,
                this.accommodationPersistence::saveEntityWithReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveDestinationImages(
            Long destinationId,
            ImageApproveDto imageApprove,
            UserDetails userDetails,
            String folder
    ) {
        return this.reviewService.approveEntityImages(
                destinationId,
                this.destinationQueryBuilder::getDestinationWithImagesAndImageReviewerById,
                imageApprove,
                userDetails,
                folder,
                this.destinationPersistence::saveEntityWithReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Page<DestinationForApprovalProjection> getAllDestinationForApproval(Pageable pageable) {
        return this.destinationQueryBuilder
                .getAllDestinationsByDestinationStatus(SuperUserReviewStatusEnum.PENDING, pageable);
    }

    private HikingTrailEntity updateTrailStatusToApprovedIfEligibleAndReturnTrail(Long trailId, HikingTrailEntity currentTrail) {
        if (currentTrail.getStatus() == StatusEnum.APPROVED
                && this.imageQueryBuilder.getCountOfNonApprovedImagesByTrailId(trailId) == 0) {
            currentTrail.setEntityStatus(SuperUserReviewStatusEnum.APPROVED);
            currentTrail = this.trailPersistence.saveEntityWithReturn(currentTrail);
        }
        return currentTrail;
    }
}
