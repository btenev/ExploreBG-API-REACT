package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HikingTrailUpdateEndPointDto(
        @NotNull(message = "err-end-point-required")
        @Pattern(
                regexp = "^[A-Za-z]+(\\s?[A-Za-z]+)*$",
                message = "err-place-regex"
        )
        @Size(
                max = 30,
                min = 3,
                message = "err-place-length"
        )
        String endPoint
) {
}
