package bg.exploreBG.model.dto;

import java.util.List;

public record HikingTrailDetailsDto(Long id,
                                    String startPoint,
                                    String endPoint,
                                    double totalDistance,
                                    String trailInfo,
                                    String imageUrl,
                                    String seasonVisited,
                                    boolean waterAvailable,
                                    List<AccommodationBasicDto> availableHuts,
                                    int trailDifficulty,
                                    List<String> activity,
                                    List<CommentsDto> comments,
                                    double elevationGained,
                                    String nextTo) {
}
