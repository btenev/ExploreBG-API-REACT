package bg.exploreBG.model.dto.gpxFile.validate;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record GpxApproveDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        Boolean approved
) {
}
