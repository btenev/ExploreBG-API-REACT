package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public SuperUserService(
            HikingTrailService hikingTrailService,
            UserService userService,
            ReviewService reviewService,
            ImageService imageService,
            HikingTrailMapper hikingTrailMapper
    ) {
        this.hikingTrailService = hikingTrailService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.imageService = imageService;
        this.hikingTrailMapper = hikingTrailMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean toggleTrailReviewClaim(
            Long id,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailService.getTrailById(id);
        UserEntity loggedUser = this.userService.getUserEntityByEmail(userDetails.getUsername());

        if (reviewBoolean.review()) {
            this.reviewService.handleClaimReview(currentTrail, loggedUser);
        } else {
            this.reviewService.handleCancelClaim(currentTrail, loggedUser);
        }

        this.hikingTrailService.saveTrailWithoutReturn(currentTrail);
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
            Long id,
            HikingTrailCreateOrReviewDto trailCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailService.getTrailById(id);
        validateTrailApproval(currentTrail, userDetails);

        updateTrailFields(currentTrail, trailCreateOrReview);
        /*TODO: TrailStatus to be updated if no images and no gpx with status PENDING or REVIEWED to APPROVED*/
        currentTrail.setStatus(StatusEnum.APPROVED);

        if (areAllApproved(currentTrail.getGpxFile(), currentTrail.getImages())) {
            currentTrail.setTrailStatus(SuperUserReviewStatusEnum.APPROVED);
        }

        this.hikingTrailService.saveTrailWithoutReturn(currentTrail);

        return true;
    }

    public boolean toggleTrailReviewImagesClaim(
            Long trailId,
            ReviewBooleanDto reviewBoolean,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = this.hikingTrailService.getTrailWithImagesById(trailId);

        UserEntity loggedUser = this.userService.getUserEntityByEmail(userDetails.getUsername());
        List<String> errorMessages = new ArrayList<>();

        List<ImageEntity> images = currentTrail.getImages();

        if (images == null || images.isEmpty()) {
            throw new AppException("No images available for review.", HttpStatus.BAD_REQUEST);
        }

        if (reviewBoolean.review()) {
            images.forEach(image -> handleClaimWithErrorHandling(image, loggedUser, errorMessages));
        } else {
            images.forEach(image -> handleCancelClaimWithErrorHandling(image, loggedUser, errorMessages));
        }

        this.imageService.saveImagesWithoutReturn(images);

        if (!errorMessages.isEmpty()) {
            throw new AppException(String.join(", ", errorMessages), HttpStatus.BAD_REQUEST);
        }

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

    private void validateTrailApproval(
            HikingTrailEntity currentTrail,
            ExploreBgUserDetails userDetails
    ) {
        StatusEnum status = currentTrail.getStatus();
        String reviewedByUserProfile = currentTrail.getReviewedBy() != null ? currentTrail.getReviewedBy().getUsername() : null;

        if (reviewedByUserProfile == null) {
            throw new AppException("A pending item can not be approved!", HttpStatus.BAD_REQUEST);
        }

        if (status.equals(StatusEnum.REVIEW) && !reviewedByUserProfile.equals(userDetails.getProfileName())) {
            throw new AppException("The item has already been claimed by another user! You can not approved it!", HttpStatus.BAD_REQUEST);
        }

        if (status.equals(StatusEnum.APPROVED)) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }
    }

    private void updateTrailFields(
            HikingTrailEntity currentTrail,
            HikingTrailCreateOrReviewDto trailCreateOrReview
    ) {
        boolean isUpdated =
                this.hikingTrailService.updateFieldIfDifferent(currentTrail::getStartPoint, currentTrail::setStartPoint, trailCreateOrReview.startPoint()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getEndPoint, currentTrail::setEndPoint, trailCreateOrReview.endPoint()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getTotalDistance, currentTrail::setTotalDistance, trailCreateOrReview.totalDistance()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getTrailInfo, currentTrail::setTrailInfo, trailCreateOrReview.trailInfo()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getSeasonVisited, currentTrail::setSeasonVisited, trailCreateOrReview.seasonVisited()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getWaterAvailable, currentTrail::setWaterAvailable, trailCreateOrReview.waterAvailable()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getTrailDifficulty, currentTrail::setTrailDifficulty, trailCreateOrReview.trailDifficulty()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getActivity, currentTrail::setActivity, trailCreateOrReview.activity()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getElevationGained, currentTrail::setElevationGained, trailCreateOrReview.elevationGained()) ||
                        this.hikingTrailService.updateFieldIfDifferent(currentTrail::getNextTo, currentTrail::setNextTo, trailCreateOrReview.nextTo()) ||

                        this.hikingTrailService.updateAccommodationList(currentTrail, trailCreateOrReview.availableHuts()) ||
                        this.hikingTrailService.updateDestinationList(currentTrail, trailCreateOrReview.destinations());

        if (isUpdated) {
            currentTrail.setModificationDate(LocalDateTime.now());
        }
    }

    private boolean areAllApproved(GpxEntity gpx, List<ImageEntity> images) {
        /* TODO: implement when we add logic for gpx status
        if (gpx != null && gpx.getStatus() != StatusEnum.APPROVED) {
            return false;
        }
        */

        if (images == null || images.isEmpty()) {
            return true;
        }

        for (ImageEntity image : images) {
            if (image.getStatus() != StatusEnum.APPROVED) {
                return false;
            }
        }
        return true;
    }

    private void handleClaimWithErrorHandling(
            ImageEntity image,
            UserEntity loggedUser,
            List<String> errorMessages
    ) {
        try {
            this.reviewService.handleClaimReview(image, loggedUser);
        } catch (AppException e) {
            errorMessages.add("Image ID " + image.getId() + ": " + e.getMessage());
        }
    }

    private void handleCancelClaimWithErrorHandling(
            ImageEntity image,
            UserEntity loggedUser,
            List<String> errorMessages
    ) {
        try {
            this.reviewService.handleCancelClaim(image, loggedUser);
        } catch (AppException e) {
            errorMessages.add("Image ID " + image.getId() + ": " + e.getMessage());
        }
    }
}
