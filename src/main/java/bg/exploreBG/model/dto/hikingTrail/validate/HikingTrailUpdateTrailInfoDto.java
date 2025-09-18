package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HikingTrailUpdateTrailInfoDto(
        @NotNull(message = "Please provide your trail description.")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@\"]*$",
                message = "Your trail description can only contain letters (A-Z, a-z), numbers (0-9), spaces, and these symbols: ( ) : ; ' \" ` ? ! - . , or new lines."
        )
        @Size(
                max = 3000,
                message = "Your trail description must not exceed {max} characters."
        )
        String trailInfo
) {
}
