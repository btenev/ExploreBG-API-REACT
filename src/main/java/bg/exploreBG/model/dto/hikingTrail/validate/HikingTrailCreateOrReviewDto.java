package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.deserializer.SuitableForEnumDeserializer;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import bg.exploreBG.model.validation.ValidPlaceName;
import bg.exploreBG.updatable.UpdatableEntityDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

public record HikingTrailCreateOrReviewDto(
        @NotNull(message = "Please enter the start point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "The start point"
        )
        String startPoint,

        @NotNull(message = "Please enter the end point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "The end point"
        )
        String endPoint,

        @Positive(message = "Total distance must be a number greater than 0.")
        Double totalDistance,

        @NotNull(message = "Please provide a short description of the trail.")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@\"]*$",
                message = "Valid characters include uppercase and lowercase letters (A-Z, a-z), numbers (0-9), spaces, and the following symbols: ( ) : ; ' \" ` ? ! - . , new line."
        )
        @Size(
                max = 3000,
                message = "Trail info text must not exceed {max} characters."
        )
        String trailInfo,

        @NotNull(message = "Please specify the season you visited.")
        SeasonEnum seasonVisited,

        @NotNull(message = "Please specify whether there is an available water source.")
        WaterAvailabilityEnum waterAvailability,

        @NotNull(message = "Please specify the trail difficulty level.")
        DifficultyLevelEnum trailDifficulty,

        @JsonDeserialize(using = SuitableForEnumDeserializer.class)
        List<SuitableForEnum> activity,

        @Positive(message = "Elevation gained must be a number greater than 0.")
        Integer elevationGained,

        @NotNull(message = "Please enter the village/town/city name near the trail.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "The village/town/city name"
        )
        String nextTo,

        Set<DestinationIdDto> destinations,

        Set<AccommodationIdDto> availableHuts
) implements UpdatableEntityDto<HikingTrailEntity> {
}



