package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.DecimalMin;

public record AccommodationUpdatePricePerBed(
        @DecimalMin(value = "0.01", inclusive = true, message = "Price per bed must be greater than 0.")
        Double pricePerBed
) {
}
