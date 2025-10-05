package bg.exploreBG.interfaces.base;

import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;

public interface HasEntityStatus {
    void setEntityStatus(SuperUserReviewStatusEnum status);
}
