package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.Positive;

public record HikingTrailUpdateTotalDistanceDto(
        @Positive(message = "Your total distance must be a number greater than 0.")
        Double totalDistance
) {
}
