package bg.exploreBG.model.dto.hike.validate;

import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.NotNull;

public record HikeUpdateNextToDto(
        @NotNull(message = "Please enter the village/town/city near your hike.")
        @ValidPlaceName(
                max = 20,
                min = 3,
                fieldName = "Your village/town/city name"
        )
        String nextTo
) {
}
