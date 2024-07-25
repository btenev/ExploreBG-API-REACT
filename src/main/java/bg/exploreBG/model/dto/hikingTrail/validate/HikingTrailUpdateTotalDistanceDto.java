package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.Positive;

public record HikingTrailUpdateTotalDistanceDto(
        @Positive(message = "err-total-distance")
        Double totalDistance
) {
}
