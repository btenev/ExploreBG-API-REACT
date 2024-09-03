package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.*;
import bg.exploreBG.serializer.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public record HikingTrailDetailsDto(
        Long id,
        String startPoint,
        String endPoint,
        Double totalDistance,
        String trailInfo,
        List<ImageIdPlusUrlDto> images,
        @JsonSerialize(using = SeasonEnumSerializer.class)
        SeasonEnum seasonVisited,
        @JsonSerialize(using = WaterAvailabilityEnumSerializer.class)
        WaterAvailabilityEnum waterAvailable,
        List<AccommodationBasicDto> availableHuts,
        @JsonSerialize(using = DifficultyLevelEnumSerializer.class)
        DifficultyLevelEnum trailDifficulty,
        @JsonSerialize(using = SuitableForEnumSerializer.class)
        List<SuitableForEnum> activity,
        List<CommentDto> comments,
        Integer elevationGained,
        String nextTo,
        UserBasicInfo createdBy,
        String gpxUrl,
        @JsonSerialize(using = StatusEnumSerializer.class)
        StatusEnum trailStatus,
        List<DestinationBasicDto> destinations
) {
}
