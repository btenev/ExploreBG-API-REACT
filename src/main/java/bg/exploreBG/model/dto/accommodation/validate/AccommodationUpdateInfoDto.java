package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.validation.DescriptionField;

public record AccommodationUpdateInfoDto(
        @DescriptionField(max = 800)
        String accommodationInfo
) {
}
