package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.enums.AccommodationTypeEnum;
import jakarta.validation.constraints.NotNull;

public record AccommodationUpdateTypeDto(
        @NotNull(message = "Please specify the accommodation type.")
        AccommodationTypeEnum type
) {
}
