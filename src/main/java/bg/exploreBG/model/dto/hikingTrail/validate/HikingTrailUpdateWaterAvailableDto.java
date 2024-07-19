package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.enums.WaterAvailabilityEnum;

public record HikingTrailUpdateWaterAvailableDto(
        WaterAvailabilityEnum waterAvailable
) {
}
