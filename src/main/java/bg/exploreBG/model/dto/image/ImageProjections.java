package bg.exploreBG.model.dto.image;

import bg.exploreBG.model.dto.user.UserIdNameProjection;
import bg.exploreBG.model.enums.StatusEnum;

public interface ImageProjections {
    Long getId();
    StatusEnum getImageStatus();
    UserIdNameProjection getReviewedBy();
}
