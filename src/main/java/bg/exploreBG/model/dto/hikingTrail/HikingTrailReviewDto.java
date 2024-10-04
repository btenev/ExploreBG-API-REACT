package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.image.ImageBasicDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.*;
import bg.exploreBG.serializer.*;
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
        @JsonSerialize(using = StatusEnumSerializer.class)
        StatusEnum detailsStatus,
        UserBasicInfo reviewedBy,
        List<DestinationBasicDto> destinations,
        List<ImageBasicDto> images
) {
}