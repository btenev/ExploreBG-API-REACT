package bg.exploreBG.model.dto;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;

public record LikeBooleanDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        @NotNull(message = "Please specify whether you like the item.")
        Boolean like
) {
}
