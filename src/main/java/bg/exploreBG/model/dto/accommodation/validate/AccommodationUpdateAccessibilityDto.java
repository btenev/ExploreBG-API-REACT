package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.enums.AccessibilityEnum;

public record AccommodationUpdateAccessibilityDto(
        AccessibilityEnum access
) {
}
