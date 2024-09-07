package bg.exploreBG.model.dto.image.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ImageMainUpdateDto(
        @NotNull
        @Positive
        Long imageId
) {
}
