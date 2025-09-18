package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccommodationUpdatePhoneNumberDto(
        @Size(min = 10, max = 13, message = "Your phone number must be between 10 and 13 characters long.")
        @Pattern(
                regexp = "^(?:\\+359|0)(87|88|89|98|99)\\d{7}$",
                message = "Your phone number format is invalid. Must start with +359 or 0 and use operator codes 87, 88, 89, 98, 99."
        )
        String phoneNumber
) {
}
