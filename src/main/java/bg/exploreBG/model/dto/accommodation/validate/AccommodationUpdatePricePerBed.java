package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.Positive;

public record AccommodationUpdatePricePerBed(
        @Positive(message = "Price per bed must be greater than 0")
        Double pricePerBed
) {
}
