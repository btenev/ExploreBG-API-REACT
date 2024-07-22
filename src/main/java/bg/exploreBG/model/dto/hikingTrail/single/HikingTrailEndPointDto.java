package bg.exploreBG.model.dto.hikingTrail.single;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record HikingTrailEndPointDto(
        String endPoint
) {
}
