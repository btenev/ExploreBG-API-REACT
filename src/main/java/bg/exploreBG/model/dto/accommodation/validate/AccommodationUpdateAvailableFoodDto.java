package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record AccommodationUpdateAvailableFoodDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        Boolean availableFood
) {
}
