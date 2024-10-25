package bg.exploreBG.reviewable;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import org.springframework.http.HttpStatus;

public interface ReviewableEntity {
    StatusEnum getStatus();

    void setStatus(StatusEnum status);

    UserEntity getReviewedBy();

    void setReviewedBy(UserEntity reviewedBy);

    default void validateForApproval(ExploreBgUserDetails userDetails) {
        StatusEnum status = this.getStatus();
        String reviewedByUserProfile = this.getReviewedBy() != null ? this.getReviewedBy().getUsername() : null;

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
}
