package bg.exploreBG.model.dto.hikingTrail.validate;

import jakarta.validation.constraints.Positive;

public record HikingTrailUpdateTotalDistanceDto(
        @Positive(message = "Total distance must be greater than 0!")
        //TODO: custom deserialization to make sure its number?????
        Double totalDistance
) {
}
