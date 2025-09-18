package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.enums.AccessibilityEnum;
import jakarta.validation.constraints.NotNull;

public record AccommodationUpdateAccessibilityDto(
        @NotNull(message = "Please specify the accessibility (On foot or By car).")
        AccessibilityEnum access
) {
}
