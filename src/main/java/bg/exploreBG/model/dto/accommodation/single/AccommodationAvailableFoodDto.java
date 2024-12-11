package bg.exploreBG.model.dto.accommodation.single;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.time.LocalDateTime;

public record AccommodationAvailableFoodDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        Boolean availableFood,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
