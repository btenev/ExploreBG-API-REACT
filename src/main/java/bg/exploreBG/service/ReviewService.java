package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.entity.BaseEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.reviewable.ReviewableEntity;
import bg.exploreBG.reviewable.ReviewableWithImages;
import bg.exploreBG.updatable.UpdatableEntity;
import bg.exploreBG.updatable.UpdatableEntityDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ReviewService {
    private final ImageService imageService;
    private final UserService userService;
    private final EntityUpdateService entityUpdateService;

    public ReviewService(
            ImageService imageService,
            UserService userService,
            EntityUpdateService entityUpdateService
    ) {
        this.imageService = imageService;
        this.userService = userService;
        this.entityUpdateService = entityUpdateService;
    }

    public <T extends BaseEntity> void handleClaimReview(T entity, UserEntity loggedUser) {
        StatusEnum entityStatus = entity.getStatus();

        if (entityStatus == StatusEnum.REVIEW) {
            if (Objects.equals(entity.getReviewedBy().getUsername(), loggedUser.getUsername())) {
                throw new AppException("You have already claimed this item for review!", HttpStatus.BAD_REQUEST);
            } else {
                throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
            }
        }

        if (entityStatus == StatusEnum.APPROVED) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }

        validateApprovedStatus(entityStatus);

        entity.setStatus(StatusEnum.REVIEW);
        entity.setReviewedBy(loggedUser);
    }

    public <T extends BaseEntity> void handleCancelClaim(T entity, UserEntity loggedUser) {
        StatusEnum entityStatus = entity.getStatus();

        if (entityStatus == StatusEnum.PENDING) {
            throw new AppException("You cannot cancel the review for an item you haven't claimed!", HttpStatus.BAD_REQUEST);
        }

        if (entityStatus == StatusEnum.REVIEW) {
            if (Objects.equals(entity.getReviewedBy().getUsername(), loggedUser.getUsername())) {
                entity.setStatus(StatusEnum.PENDING);
                entity.setReviewedBy(null);
            } else {
                throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
            }
        }

        validateApprovedStatus(entityStatus);
    }

    public <T extends ReviewableEntity & UpdatableEntity> void validateAndApproveEntity(
            T entity,
            UpdatableEntityDto<T> dto,
            ExploreBgUserDetails loggedUser
    ) {
        entity.validateForApproval(loggedUser);

        if (dto != null) {
            this.entityUpdateService.updateFieldsIfNecessary(entity, dto);
        }

        entity.setStatus(StatusEnum.APPROVED);
    }

    public <T extends ReviewableWithImages> T saveApprovedImages(
            T entity,
            ImageApproveDto approveDto,
            UserDetails userDetails
    ) {
        UserEntity reviewer = this.userService.getUserEntityByEmail(userDetails.getUsername());

        List<ImageEntity> approved = entity.approvalWithValidation(approveDto.imageIds(), reviewer);

        this.imageService.saveImagesWithoutReturn(approved);

        return entity;
    }

    private <T extends BaseEntity> void validateItemApproval(
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
            throw new AppException("The item has already been claimed by another user! You cannot approve it!", HttpStatus.BAD_REQUEST);
        }

        validateApprovedStatus(status);
    }

    private void validateApprovedStatus(StatusEnum entityStatus) {
        if (entityStatus == StatusEnum.APPROVED) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }
    }
}
