package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccommodationUpdateInfoDto(
        @NotNull(message = "Please enter a short description of the accommodation!")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@\"]*$",
                message = "Accommodation info allowed symbols are upper and lower letters, digits 0 to 9, dot, comma, dash, new line, brackets empty space!"
                /*message = "err-trail-info-regex"*/
        )
        @Size(
                max = 3000,
                message = "err-trail-info-max-length"
        )
        String accommodationInfo
) {
}
