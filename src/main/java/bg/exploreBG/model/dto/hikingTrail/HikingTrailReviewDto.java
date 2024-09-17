package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import bg.exploreBG.serializer.DifficultyLevelEnumSerializer;
import bg.exploreBG.serializer.SeasonEnumSerializer;
import bg.exploreBG.serializer.SuitableForEnumSerializer;
import bg.exploreBG.serializer.WaterAvailabilityEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public record HikingTrailReviewDto(
        Long id,
        String startPoint,
        String endPoint,
        Double totalDistance,
        String trailInfo,
        @JsonSerialize(using = SeasonEnumSerializer.class)
        SeasonEnum seasonVisited,
        @JsonSerialize(using = WaterAvailabilityEnumSerializer.class)
        WaterAvailabilityEnum waterAvailable,
        List<AccommodationBasicDto> availableHuts,
        @JsonSerialize(using = DifficultyLevelEnumSerializer.class)
        DifficultyLevelEnum trailDifficulty,
        @JsonSerialize(using = SuitableForEnumSerializer.class)
        List<SuitableForEnum> activity,
        Integer elevationGained,
        String nextTo,
        List<DestinationBasicDto> destinations
) {
}