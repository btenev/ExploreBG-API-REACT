package bg.exploreBG.model.dto.destination.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DestinationUpdateInfoDto(
        @NotNull(message = "Please enter a short description of the destination!")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n]*$",
                message = "Destination info allowed symbols are upper and lower letters, digits 0 to 9, dot, comma, dash, new line, empty space!"
        )
        @Size(
                max = 800,
                message = "The trail info text shouldn't exceed 800 symbols"
        )
        String destinationInfo
) {
}
