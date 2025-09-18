package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.NotNull;

public record HikingTrailUpdateEndPointDto(
        @NotNull(message = "Please enter your end point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your end point"
        )
        String endPoint
) {
}
