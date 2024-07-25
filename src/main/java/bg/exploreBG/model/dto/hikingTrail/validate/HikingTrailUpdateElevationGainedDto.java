package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.Positive;

public record HikingTrailUpdateElevationGainedDto(
        @Positive(message = "err-total-elevation")
        Integer elevationGained
) {
}
