package bg.exploreBG.model.dto.destination.validate;

import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.NotNull;

public record DestinationUpdateDestinationNameDto(
        @NotNull(message = "Please enter your destination name.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your destination name"
        )
        String destinationName
) {
}
