package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AccommodationCreateDto(
        @NotNull(message = "Accommodation name can not be blank!")
        @Pattern(
                regexp = "^[A-Za-z]+\\s?[A-Za-z]+$",
                message = "Accommodation name allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
        )
        @Size(max = 30)
        String accommodationName,

        // TODO: regex - phone number
        String phoneNumber,

        //TODO: regex - site url
        String site,

        @NotNull(message = "Please enter a short description of the accommodation!")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`]*$",
                message = "Accommodation info allowed symbols are upper and lower letters, digits 0 to 9, dot, comma, dash, new line, brackets empty space!"
        )
        @Size(
                max = 800,
                message = "The accommodation info text shouldn't exceed 800 symbols"
        )
        String accommodationInfo,

        @Positive(message = "Bed capacity must be greater than 0")
        Integer bedCapacity,

        @Positive(message = "Price per bed must be greater than 0")
        Double pricePerBed,

        Boolean foodAvailable,

        AccessibilityEnum access,

        AccommodationTypeEnum type,

        @NotNull(message = "Please, enter town or city name that is close to the trail!")
        @Pattern(
                regexp = "^[A-Za-z]{3,15}$",
                message = "City/town name should contain from 3 to 15 letters!"
        )
        String nextTo
) {
}
