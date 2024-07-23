package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;

import java.util.List;

public record HikingTrailDetailsDto(
        Long id,
        String startPoint,
        String endPoint,
        Double totalDistance,
        String trailInfo,
        String imageUrl,
        String seasonVisited,
        String waterAvailable,
        List<AccommodationBasicDto> availableHuts,
        int trailDifficulty,
        List<String> activity,
        List<CommentDto> comments,
        Integer elevationGained,
        String nextTo,
        UserBasicInfo createdBy,
        List<DestinationBasicDto> destinations) {
}
