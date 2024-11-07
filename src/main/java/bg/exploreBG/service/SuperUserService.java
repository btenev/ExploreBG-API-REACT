package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.EntityIdsToDeleteDto;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailImageStatusAndGpxFileStatus;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.entity.GpxEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SuperUserService {
    private static final Logger logger = LoggerFactory.getLogger(SuperUserService.class);
    private final HikingTrailService hikingTrailService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final ImageService imageService;
    private final HikingTrailMapper hikingTrailMapper;
    private final GpxService gpxService;

    public SuperUserService(
            HikingTrailService hikingTrailService,
            UserService userService,
            ReviewService reviewService,
            ImageService imageService,
            HikingTrailMapper hikingTrailMapper,
            GpxService gpxService
    ) {
        this.hikingTrailService = hikingTrailService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.imageService = imageService;
        this.hikingTrailMapper = hikingTrailMapper;
        this.gpxService = gpxService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleTrailClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailService.getTrailById(trailId);
        UserEntity reviewer = this.userService.getUserEntityByEmail(userDetails.getUsername());

        this.reviewService.toggleEntityClaim(currentTrail, reviewBoolean.review(), reviewer);

        this.hikingTrailService.saveTrailWithoutReturn(currentTrail);
        return true;
    }

    public boolean toggleTrailGpxFileClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailService.getTrailWithGpxFile(trailId);

        GpxEntity gpxFile = currentTrail.getGpxFile();
        if (gpxFile == null) {
            throw new AppException("Cannot claim a GPX file that doesn't exist!", HttpStatus.BAD_REQUEST);
        }

        UserEntity reviewer = this.userService.getUserEntityByEmail(userDetails.getUsername());

        this.reviewService.toggleEntityClaim(gpxFile, reviewBoolean.review(), reviewer);
        this.gpxService.saveGpxFileWithoutReturn(gpxFile);

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public HikingTrailReviewDto reviewTrail(
            Long trailId,
            ExploreBgUserDetails userDetails,
            SuperUserReviewStatusEnum supeStatus
    ) {
        HikingTrailEntity currentTrail = hikingTrailService.getTrailWithImagesByIdAndTrailStatus(trailId, supeStatus);

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

        throw new AppException("Item with invalid status for review!", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean approveTrail(
            Long trailId,
            HikingTrailCreateOrReviewDto trailCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailService.getTrailById(trailId);
        this.reviewService.validateAndApproveEntity(currentTrail, trailCreateOrReview, userDetails);

        HikingTrailImageStatusAndGpxFileStatus statuses =
                this.hikingTrailService.getTrailImageStatusAndGpxFileStatus(trailId);
        logger.info("Hiking trail image and gpx status: {}", statuses);
        if ((statuses.imageStatus() == null || !statuses.imageStatus().equals("NOT_APPROVED") && StatusEnum.valueOf(statuses.imageStatus()) == StatusEnum.APPROVED)
                && statuses.gpxFileStatus() == null || statuses.gpxFileStatus() == StatusEnum.APPROVED) {
            currentTrail.setTrailStatus(SuperUserReviewStatusEnum.APPROVED);
        }

        this.hikingTrailService.saveTrailWithoutReturn(currentTrail);

        return true;
    }

    public boolean toggleTrailImageClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailService.getTrailWithImagesById(trailId);

        this.reviewService.toggleImageClaimAndSave(currentTrail, reviewBoolean, userDetails);

        return true;
    }

    public boolean approveTrailImages(
            Long trailId,
            ImageApproveDto imageApproveDto,
            UserDetails userDetails,
            String folder
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailService.getTrailWithImagesAndImageReviewerAndGpxFileById(trailId);

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

        this.hikingTrailService.saveTrailWithoutReturn(currentTrail);

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
