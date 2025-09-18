package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.PositiveOrZero;

public record AccommodationUpdateBedCapacityDto(
        @PositiveOrZero(message = "Bed capacity cannot be negative.")
        Integer bedCapacity
) {
}
