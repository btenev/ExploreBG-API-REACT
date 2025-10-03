package bg.exploreBG.interfaces;

import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;

import java.time.LocalDateTime;

public interface UpdatableEntity {
    void setStatus(StatusEnum status);
    void setEntityStatus(SuperUserReviewStatusEnum status);
    void setModificationDate(LocalDateTime modificationDate);
    LocalDateTime getModificationDate();
}
