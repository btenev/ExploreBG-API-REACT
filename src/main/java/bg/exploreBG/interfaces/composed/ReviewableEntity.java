package bg.exploreBG.interfaces.composed;

import bg.exploreBG.interfaces.base.HasStatus;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;

public interface ReviewableEntity extends HasStatus {
    StatusEnum getStatus();
    UserEntity getReviewedBy();
    void setReviewedBy(UserEntity reviewedBy);
}
