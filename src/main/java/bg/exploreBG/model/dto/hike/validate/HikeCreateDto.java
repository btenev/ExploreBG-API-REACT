package bg.exploreBG.model.dto.hike.validate;

import bg.exploreBG.model.validation.DescriptionField;
import bg.exploreBG.model.validation.ValidPlaceName;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record HikeCreateDto(

        @NotNull(message = "Please enter your start point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your start point"
        )
        String startPoint,

        @NotNull(message = "Please enter your end point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your end point"
        )
        String endPoint,

        @NotNull(message = "Please enter your hike date.")
        @Future(message = "The hike date must be in the future.")
        LocalDateTime hikeDate,

        Long trailId,

        @DescriptionField(max = 3000)
        String hikeInfo,

        @NotNull(message = "Please enter the village/town/city near your hike.")
        @ValidPlaceName(
                max = 20,
                min = 3,
                fieldName = "Your village/town/city name"
        )
        String nextTo
) {
}
