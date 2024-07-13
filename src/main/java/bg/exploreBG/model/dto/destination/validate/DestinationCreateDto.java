package bg.exploreBG.model.dto.destination.validate;

import bg.exploreBG.model.enums.DestinationTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DestinationCreateDto(
        @NotNull(message = "Destination name can not be blank!")
        @Pattern(
                regexp = "^[A-Za-z]+\\s?[A-Za-z]+$",
                message = "Destination name allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
        )
        @Size(max = 30)
        String destinationName,

        @NotNull(message = "Location can not be blank!")
        @Pattern(
                regexp = "^[A-za-z]+(\\s?[A-Za-z]+)*$",
                message = "Location allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
        )
        @Size(max = 30)
        String location,

        @NotNull(message = "Please enter a short description of the destination!")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n]*$",
                message = "Destination info allowed symbols are upper and lower letters, digits 0 to 9, dot, comma, dash, new line, empty space!"
        )
        @Size(
                max = 800,
                message = "The trail info text shouldn't exceed 800 symbols"
        )
        String destinationInfo,

        @NotNull(message = "Please, enter town or city name that is close to the trail!")
        @Pattern(
                regexp = "^[A-Za-z]{3,15}$",
                message = "City/town name should contain from 3 to 15 letters!"
        )
        String nextTo,

        DestinationTypeEnum type
) {
}
