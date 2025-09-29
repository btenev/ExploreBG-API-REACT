package bg.exploreBG.model.dto.destination.validate;

import bg.exploreBG.model.enums.DestinationTypeEnum;
import jakarta.validation.constraints.NotNull;

public record DestinationUpdateTypeDto(
        @NotNull(message = "Please specify the destination type.")
        DestinationTypeEnum type
) {
}
