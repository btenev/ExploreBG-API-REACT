package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HikingTrailUpdateTrailInfoDto(
        @NotNull(message = "Please provide a short description of the trail.")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@\"]*$",
                message = "Valid characters include uppercase and lowercase letters (A-Z, a-z), numbers (0-9), spaces, and the following symbols: ( ) : ; ' \" ` ? ! - . , new line."
        )
        @Size(
                max = 3000,
                message = "Trail info text must not exceed {max} characters."
        )
        String trailInfo
) {
}
