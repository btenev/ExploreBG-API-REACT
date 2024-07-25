package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HikingTrailUpdateTrailInfoDto(
        @NotNull(message = "err-trail-info-required")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@]*$",
                message = "err-trail-info-regex"
        )
        @Size(
                max = 3000,
                message = "err-trail-info-max-length"
        )
        String trailInfo
) {
}
