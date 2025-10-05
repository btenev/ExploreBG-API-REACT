package bg.exploreBG.model.dto.hike.validate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record HikeUpdateDateDto(
        @NotNull(message = "Please enter your hike date.")
        @Future(message = "The hike date must be in the future.")
        LocalDateTime hikeDate
) {
}
