package bg.exploreBG.model.dto.hikingTrail;

public record HikingTrailBasicLikesDto(
        Long id,
        String trailName,
        String trailInfo,
        String imageUrl,
        boolean likedByUser
) {
}
