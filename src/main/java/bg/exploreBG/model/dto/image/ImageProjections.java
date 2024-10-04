package bg.exploreBG.model.dto.image;

import bg.exploreBG.model.dto.user.UserIdNameProjection;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface ImageProjections {
    Long getId();
    @JsonSerialize(using = StatusEnumSerializer.class)
    StatusEnum getImageStatus();
    UserIdNameProjection getReviewedBy();
}
