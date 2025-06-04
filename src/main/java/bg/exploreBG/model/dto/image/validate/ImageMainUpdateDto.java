package bg.exploreBG.model.dto.image.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ImageMainUpdateDto(
        @NotNull(message = "Image ID must not be null.")
        @Positive(message = "Image ID must be a positive number.")
        Long imageId
) {
}
