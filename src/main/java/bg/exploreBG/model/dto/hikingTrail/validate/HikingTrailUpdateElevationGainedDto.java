package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.Positive;

public record HikingTrailUpdateElevationGainedDto(
        @Positive(message = "Your elevation gained must be greater than 0.")
        Integer elevationGained
) {
}
