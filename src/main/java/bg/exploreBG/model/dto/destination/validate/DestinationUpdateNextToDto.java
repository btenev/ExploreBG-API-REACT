package bg.exploreBG.model.dto.destination.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DestinationUpdateNextToDto(
        @NotNull(message = "Please, enter town or city name that is close to the trail!")
        @Pattern(
                regexp = "^[A-Za-z]{3,15}$",
                message = "City/town name should contain from 3 to 15 letters!"
        )
        String nextTo
) {
}
