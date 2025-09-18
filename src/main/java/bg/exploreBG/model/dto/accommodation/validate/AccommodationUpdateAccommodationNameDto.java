package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.NotNull;

public record AccommodationUpdateAccommodationNameDto(
        @NotNull(message = "Please enter your accommodation name.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your accommodation name"
        )
        String accommodationName
) {
}
