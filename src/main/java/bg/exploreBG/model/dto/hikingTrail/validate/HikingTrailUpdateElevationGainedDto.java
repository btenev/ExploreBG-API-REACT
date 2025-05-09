package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.Positive;

public record HikingTrailUpdateElevationGainedDto(
        @Positive(message = "Elevation gained must be a number greater than 0.")
        Integer elevationGained
) {
}
