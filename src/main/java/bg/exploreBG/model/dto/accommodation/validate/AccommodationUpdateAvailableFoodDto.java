package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.enums.FoodAvailabilityEnum;
import jakarta.validation.constraints.NotNull;

public record AccommodationUpdateAvailableFoodDto(
        @NotNull(message = "Please specify if food is available.")
        FoodAvailabilityEnum availableFood
) {
}
