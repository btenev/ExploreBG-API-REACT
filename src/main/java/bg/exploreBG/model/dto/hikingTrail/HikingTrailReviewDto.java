package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.gpxFile.GpxBasicDto;
import bg.exploreBG.model.dto.image.ImageBasicDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.*;
import bg.exploreBG.serializer.*;
import com.fasterxml.jackson.annotation.JsonProperty;
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
        WaterAvailabilityEnum waterAvailability,
        List<AccommodationIdAndAccommodationName> availableHuts,
        @JsonSerialize(using = DifficultyLevelEnumSerializer.class)
        DifficultyLevelEnum trailDifficulty,
        @JsonSerialize(using = SuitableForEnumSerializer.class)
        List<SuitableForEnum> activity,
        Integer elevationGained,
        String nextTo,
        @JsonSerialize(using = StatusEnumSerializer.class)
        @JsonProperty(value = "detailsStatus")
        StatusEnum status,
        UserBasicInfo reviewedBy,
        List<DestinationIdAndDestinationNameDto> destinations,
        List<ImageBasicDto> images,
        GpxBasicDto gpxFile
) {
}