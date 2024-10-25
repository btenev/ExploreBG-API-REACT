package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;

import java.util.Set;

public record HikingTrailUpdateAvailableHutsDto(
        Set<AccommodationIdDto> availableHuts
) {
}
