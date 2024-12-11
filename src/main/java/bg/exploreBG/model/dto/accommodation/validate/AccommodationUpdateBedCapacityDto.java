package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.Positive;

public record AccommodationUpdateBedCapacityDto(
        @Positive(message = "Bed capacity must be greater than 0")
        Integer bedCapacity
) {
}
