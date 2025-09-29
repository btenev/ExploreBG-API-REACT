package bg.exploreBG.model.dto.destination.validate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record DestinationUpdateLocationDto(
        @DecimalMin(value = "-90.0", message = "Your latitude cannot be less than -90.")
        @DecimalMax(value = "90.0", message = "Your latitude cannot be greater than 90.")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "Your longitude cannot be less than -180.")
        @DecimalMax(value = "180.0", message = "Your longitude cannot be greater than 180.")
        Double longitude
) {
}
