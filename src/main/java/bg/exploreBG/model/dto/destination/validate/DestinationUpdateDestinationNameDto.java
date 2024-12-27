package bg.exploreBG.model.dto.destination.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DestinationUpdateDestinationNameDto(
        @NotNull(message = "Destination name can not be blank!")
        @Pattern(
                regexp = "^[A-Za-z]+\\s?[A-Za-z]+$",
                message = "Destination name allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
        )
        @Size(max = 30)
        String destinationName
) {
}
