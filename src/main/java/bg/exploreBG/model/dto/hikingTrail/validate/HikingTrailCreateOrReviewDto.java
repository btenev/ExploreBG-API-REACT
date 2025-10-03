package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.deserializer.SuitableForEnumDeserializer;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import bg.exploreBG.model.validation.DescriptionField;
import bg.exploreBG.model.validation.ValidPlaceName;
import bg.exploreBG.interfaces.UpdatableEntityDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.Set;

public record HikingTrailCreateOrReviewDto(
        @NotNull(message = "Please enter your start point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your start point"
        )
        String startPoint,

        @NotNull(message = "Please enter your end point.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your end point"
        )
        String endPoint,

        @Positive(message = "Your total distance must be a number greater than 0.")
        Double totalDistance,

        @DescriptionField(max = 3000)
        String trailInfo,

        @NotNull(message = "Please specify the season you visited.")
        SeasonEnum seasonVisited,

        @NotNull(message = "Please specify whether water is available.")
        WaterAvailabilityEnum waterAvailability,

        @NotNull(message = "Please specify the trail difficulty level.")
        DifficultyLevelEnum trailDifficulty,

        @JsonDeserialize(using = SuitableForEnumDeserializer.class)
        List<SuitableForEnum> activity,

        @Positive(message = "Your elevation gained must be greater than 0.")
        Integer elevationGained,

        @NotNull(message = "Please enter the village/town/city near your trail.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your village/town/city name"
        )
        String nextTo,

        Set<DestinationIdDto> destinations,

        Set<AccommodationIdDto> availableHuts
) implements UpdatableEntityDto<HikingTrailEntity> {
}



