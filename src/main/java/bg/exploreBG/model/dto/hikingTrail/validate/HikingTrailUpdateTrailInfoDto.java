package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.validation.DescriptionField;

public record HikingTrailUpdateTrailInfoDto(
        @DescriptionField(max = 3000)
        String trailInfo
) {
}
