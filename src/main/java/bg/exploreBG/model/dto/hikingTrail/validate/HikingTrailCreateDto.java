package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.deserializer.SuitableForEnumDeserializer;
import bg.exploreBG.model.dto.accommodation.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;

import java.util.List;

public record HikingTrailCreateDto(
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

        @Positive(message = "Total distance must be greater than 0!")
        Double totalDistance,

        @NotNull(message = "Please enter a short description of the trail!")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n]*$",
                message = "Trail info allowed symbols are upper and lower letters, digits 0 to 9, dot, comma, dash, new line, empty space!"
        )
        @Size(
                max = 800,
                message = "The trail info text shouldn't exceed 800 symbols"
        )
        String trailInfo,

//      @EnumMatch(enumClass = SeasonEnum.class)
//      @NotNull(message = "Please, enter during which season the hike took place!")
        SeasonEnum seasonVisited,

//      @NotNull(message = "Please, enter are any water sources available!")
        WaterAvailabilityEnum waterAvailable,

//      @EnumMatch(enumClass = DifficultyLevelEnum.class)
//      @NotNull(message = "Please, rate the difficulty level of the trail with a number from 1 to 6!")
        DifficultyLevelEnum trailDifficulty,

//      @NotEmpty(message = "Please, select at least one activity!")
        @JsonDeserialize(using = SuitableForEnumDeserializer.class)
        List<SuitableForEnum> activity,

        @Positive(message = "Elevation gained must be greater than 0")
        Integer elevationGained,

        @NotNull(message = "Please, enter town or city name that is close to the trail!")
        @Pattern(
                regexp = "^[A-Za-z]{3,15}$",
                message = "City/town name should contain from 3 to 15 letters!"
        )
        String nextTo,

        List<DestinationIdDto> destinations,

        List<AccommodationIdDto> availableHuts
) {
}



