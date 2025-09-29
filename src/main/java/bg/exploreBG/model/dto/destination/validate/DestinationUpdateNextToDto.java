package bg.exploreBG.model.dto.destination.validate;

import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.NotNull;

public record DestinationUpdateNextToDto(
        @NotNull(message = "Please enter the village/town/city near your destination.")
        @ValidPlaceName(
                max = 20,
                min = 3,
                fieldName = "Your village/town/city name"
        )
        String nextTo
) {
}
