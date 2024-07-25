package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.enums.DifficultyLevelEnum;

public record HikingTrailUpdateTrailDifficultyDto(
        DifficultyLevelEnum trailDifficulty
) {
}
