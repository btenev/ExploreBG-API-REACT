package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.model.enums.DifficultyLevelEnum;
import jakarta.validation.constraints.NotNull;

public record HikingTrailUpdateTrailDifficultyDto(
        @NotNull(message = "Please specify the trail difficulty level.")
        DifficultyLevelEnum trailDifficulty
) {
}
