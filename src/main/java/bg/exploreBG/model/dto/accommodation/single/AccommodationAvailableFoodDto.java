package bg.exploreBG.model.dto.accommodation.single;

import bg.exploreBG.model.enums.FoodAvailabilityEnum;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record AccommodationAvailableFoodDto(

        String availableFood,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
