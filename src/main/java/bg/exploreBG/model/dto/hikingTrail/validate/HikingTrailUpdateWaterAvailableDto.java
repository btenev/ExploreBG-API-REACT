package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import jakarta.validation.constraints.NotNull;

public record HikingTrailUpdateWaterAvailableDto(
        @NotNull(message = "Please specify whether there is an available water source.")
        WaterAvailabilityEnum waterAvailable
) {
}
