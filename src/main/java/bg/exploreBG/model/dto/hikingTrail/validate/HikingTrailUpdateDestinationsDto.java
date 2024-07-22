package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.dto.destination.single.DestinationIdDto;

import java.util.List;

public record HikingTrailUpdateDestinationsDto(
        List<DestinationIdDto> destinations
) {
}
