package bg.exploreBG.model.dto.hike.validate;

import bg.exploreBG.model.validation.DescriptionField;

public record HikeUpdateInfoDto(
        @DescriptionField(max = 3000)
        String hikeInfo
) {
}
