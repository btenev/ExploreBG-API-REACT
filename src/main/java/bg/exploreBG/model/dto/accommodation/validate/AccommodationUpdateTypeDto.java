package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.enums.AccommodationTypeEnum;

public record AccommodationUpdateTypeDto(
        AccommodationTypeEnum type
) {
}
