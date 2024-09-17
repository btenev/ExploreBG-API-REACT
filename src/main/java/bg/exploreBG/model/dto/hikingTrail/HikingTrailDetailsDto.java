package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.*;
import bg.exploreBG.serializer.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;

public record HikingTrailDetailsDto(
        Long id,
        String startPoint,
        String endPoint,
        Double totalDistance,
        String trailInfo,
        List<ImageIdUrlIsMainDto> images,
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate,
        @JsonSerialize(using = StatusEnumSerializer.class)
        StatusEnum trailStatus,
        List<DestinationBasicDto> destinations
) {
}
