package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.model.enums.GenderEnum;

public record UpdateGenderDto(
        GenderEnum gender
) {
}
