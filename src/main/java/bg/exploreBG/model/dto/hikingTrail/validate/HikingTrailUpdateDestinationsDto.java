package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.dto.destination.single.DestinationIdDto;

import java.util.Set;

public record HikingTrailUpdateDestinationsDto(
        Set<DestinationIdDto> destinations
) {
}
