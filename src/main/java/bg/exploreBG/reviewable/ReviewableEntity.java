package bg.exploreBG.reviewable;

import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;

public interface ReviewableEntity {
    StatusEnum getStatus();

    void setStatus(StatusEnum status);

    UserEntity getReviewedBy();

    void setReviewedBy(UserEntity reviewedBy);
}
