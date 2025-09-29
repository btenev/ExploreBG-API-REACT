package bg.exploreBG.model.dto.destination.validate;

import bg.exploreBG.model.validation.DescriptionField;

public record DestinationUpdateInfoDto(
        @DescriptionField(max = 800)
        String destinationInfo
) {
}
