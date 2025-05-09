package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.NotNull;

public record HikingTrailUpdateStartPointDto(
        @NotNull(message = "Please enter the start point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "The start point"
        )
        String startPoint
) {
}
