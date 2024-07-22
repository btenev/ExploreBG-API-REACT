package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.deserializer.SuitableForEnumDeserializer;
import bg.exploreBG.model.enums.SuitableForEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record HikingTrailUpdateActivityDto(
        @JsonDeserialize(using = SuitableForEnumDeserializer.class)
        List<SuitableForEnum> activity
) {
}
