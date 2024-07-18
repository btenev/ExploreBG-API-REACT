package bg.exploreBG.model.dto.hike.validate;

import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailIdDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record HikeCreateDto(

        @NotNull(message = "Start point can not be blank!")
        @Pattern(
                regexp = "^[A-Za-z]+\\s?[A-Za-z]+$",
                message = "Start point allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
        )
        @Size(max = 30)
        String startPoint,

        @NotNull(message = "End point can not be blank!")
        @Pattern(
                regexp = "^[A-Za-z]+\\s?[A-Za-z]+$",
                message = "End point allowed symbols are upper, lower letters, zero or one empty space but not in the beginning!"
        )
        @Size(max = 30)
        String endPoint,

        @Future(message = "Hike date should be in the future!")
        LocalDate hikeDate,
        String imageUrl,
        HikingTrailIdDto hikingTrail,

        @NotNull(message = "Please enter a short description of the hike!")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:]*$",
                message = "Hike info allowed symbols are upper and lower letters, digits 0 to 9, dot, comma, dash, new line, brackets, colon, empty space!"
        )
        @Size(
                max = 800,
                message = "The Hike info text shouldn't exceed 800 symbols"
        )
        String hikeInfo,

        @NotNull(message = "Please, enter town or city name that is close to the trail!")
        @Pattern(
                regexp = "^[A-Za-z]{3,15}$",
                message = "City/town name should contain from 3 to 15 letters!"
        )
        String nextTo
) {
}
