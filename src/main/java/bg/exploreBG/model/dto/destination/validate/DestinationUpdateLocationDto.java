package bg.exploreBG.model.dto.destination.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DestinationUpdateLocationDto(
        @NotNull(message = "Location can not be blank!")
        @Pattern(
                regexp = "^[A-za-z]+(\\s?[A-Za-z]+)*$",
                message = "Location allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
        )
        @Size(max = 30)
        String location
) {
}
