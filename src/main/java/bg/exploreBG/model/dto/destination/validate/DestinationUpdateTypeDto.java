package bg.exploreBG.model.dto.destination.validate;

import bg.exploreBG.model.enums.DestinationTypeEnum;

public record DestinationUpdateTypeDto(
        DestinationTypeEnum type
) {
}
