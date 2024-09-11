package bg.exploreBG.model.dto.hikingTrail;

public record HikingTrailBasicDto(
        Long id,
        String trailName,
        String trailInfo,
        String imageUrl,
        boolean likedByUser
) {
}
