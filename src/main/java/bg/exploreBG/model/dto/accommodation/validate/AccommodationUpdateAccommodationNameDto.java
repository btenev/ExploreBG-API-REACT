package bg.exploreBG.model.dto.accommodation.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccommodationUpdateAccommodationNameDto(
        @NotNull(message = "Accommodation name can not be blank!")
        @Pattern(
                regexp = "^[A-Za-z]+(\\s?[A-Za-z]+)*$",
                message = "Accommodation name allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
                /*"err-place-regex"*/
        )
        @Size(
                max = 30,
                min = 3,
                message = "err-place-length"
        )
        String accommodationName
) {
}
