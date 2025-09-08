package bg.exploreBG.service.moderation;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.gpxFile.validate.GpxApproveDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.querybuilder.GpxQueryBuilder;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.querybuilder.ImageQueryBuilder;
import bg.exploreBG.service.GenericPersistenceService;
import bg.exploreBG.service.GpxService;
import bg.exploreBG.service.moderation.common.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TrailReviewService {
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final GpxQueryBuilder gpxQueryBuilder;
    private final ImageQueryBuilder imageQueryBuilder;
    private final ReviewService reviewService;
    private final GpxService gpxService;
    private final HikingTrailMapper hikingTrailMapper;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;
    private final GenericPersistenceService<GpxEntity> gpxPersistence;


    public TrailReviewService(
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            GpxQueryBuilder gpxQueryBuilder,
            ImageQueryBuilder imageQueryBuilder,
            ReviewService reviewService,
            GpxService gpxService,
            HikingTrailMapper hikingTrailMapper,
            GenericPersistenceService<HikingTrailEntity> trailPersistence,
            GenericPersistenceService<GpxEntity> gpxPersistence
    ) {
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.gpxQueryBuilder = gpxQueryBuilder;
        this.imageQueryBuilder = imageQueryBuilder;
        this.reviewService = reviewService;
        this.gpxService = gpxService;
        this.hikingTrailMapper = hikingTrailMapper;
        this.trailPersistence = trailPersistence;
        this.gpxPersistence = gpxPersistence;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Page<HikingTrailForApprovalProjection> getAllHikingTrailsForApproval(
            Pageable pageable
    ) {
        return this.hikingTrailQueryBuilder
                .getAllHikingTrailsByTrailStatus(SuperUserReviewStatusEnum.PENDING, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public HikingTrailReviewDto reviewTrail(
            Long trailId,
            SuperUserReviewStatusEnum supeStatus
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndTrailStatus(trailId, supeStatus);

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailReviewDto(currentTrail);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerIdByTrailId(Long trailId) {
        Long reviewerId = this.hikingTrailQueryBuilder.getReviewerId(trailId);
        return new UserIdDto(reviewerId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void claimTrail(Long trailId, UserDetails userDetails) {
        this.reviewService
                .toggleClaim(
                        trailId,
                        true,
                        userDetails,
                        this.hikingTrailQueryBuilder::getHikingTrailById,
                        this.trailPersistence::saveEntityWithoutReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unclaimTrail(Long trailId, UserDetails userDetails) {
        this.reviewService
                .toggleClaim(
                        trailId,
                        false,
                        userDetails,
                        this.hikingTrailQueryBuilder::getHikingTrailById,
                        this.trailPersistence::saveEntityWithoutReturn);
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
    public void claimTrailImages(
            Long trailId,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                trailId,
                this.hikingTrailQueryBuilder::getHikingTrailWithImagesAndImageReviewerById,
                true,
                userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unclaimTrailImages(
            Long trailId,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                trailId,
                this.hikingTrailQueryBuilder::getHikingTrailWithImagesAndImageReviewerById,
                false,
                userDetails);
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
    public void claimTrailGpxFile(
            Long trailId,
            UserDetails userDetails
    ) {
        handleGpxClaimAction(trailId, userDetails, true);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unclaimTrailGpxFile(
            Long trailId,
            UserDetails userDetails
    ) {
        handleGpxClaimAction(trailId, userDetails, false);
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

    private void handleGpxClaimAction(Long trailId, UserDetails userDetails, boolean shouldClaim) {
        HikingTrailEntity currentTrail = this.hikingTrailQueryBuilder.getHikingTrailWithGpxFileById(trailId);

        GpxEntity gpxFile = currentTrail.getGpxFile();
        if (gpxFile == null) {
            throw new AppException(
                    "Cannot " + (shouldClaim ? "claim" : "unclaim") + " a GPX file that doesn't exist!",
                    HttpStatus.BAD_REQUEST
            );
        }

        this.reviewService.toggleGpxFileClaim(gpxFile, shouldClaim, userDetails);
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
