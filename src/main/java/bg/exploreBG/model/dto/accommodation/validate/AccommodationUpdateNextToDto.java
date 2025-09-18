package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.NotNull;

public record AccommodationUpdateNextToDto(
        @NotNull(message = "Please enter the village/town/city near your accommodation.")
        @ValidPlaceName(
                max = 20,
                min = 3,
                fieldName = "Your village/town/city name"
        )
        String nextTo
) {
}