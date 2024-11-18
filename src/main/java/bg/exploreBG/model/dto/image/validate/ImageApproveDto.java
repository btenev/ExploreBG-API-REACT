package bg.exploreBG.model.dto.image.validate;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record ImageApproveDto(
        @NotNull(message = "Image IDs are required.")
        @NotEmpty(message = "Image IDs collection must contain at least one ID")
        Set<@Positive(message = "Each image id must be a positive number") Long> imageIds
) {
}
