package bg.exploreBG.model.dto;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record ReviewBooleanDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        Boolean review
) {
}
