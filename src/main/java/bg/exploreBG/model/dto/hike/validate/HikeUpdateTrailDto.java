package bg.exploreBG.model.dto.hike.validate;

import jakarta.validation.constraints.NotNull;

public record HikeUpdateTrailDto(
        @NotNull(message = "Please select a hiking trail.")
        Long id
) {
}
