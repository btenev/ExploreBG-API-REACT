package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;

import java.util.List;

public record HikingTrailUpdateAvailableHutsDto(
        List<AccommodationIdDto> availableHuts
) {
}
