package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HikingTrailUpdateTrailInfo(
        @NotNull(message = "Please enter a short description of the trail!")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n]*$",
                message = "Trail info allowed symbols are upper and lower letters, digits 0 to 9, dot, comma, dash, new line, empty space!"
        )
        @Size(
                max = 800,
                message = "The trail info text shouldn't exceed 800 symbols"
        )
        String trailInfo
) {
}
