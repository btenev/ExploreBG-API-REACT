package bg.exploreBG.model.dto.hikingTrail.single;

import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.serializer.SuperUserReviewStatusEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record EntitySuperUserReviewStatusDto(
        @JsonSerialize(using = SuperUserReviewStatusEnumSerializer.class)
        SuperUserReviewStatusEnum entityStatus
) {
}
