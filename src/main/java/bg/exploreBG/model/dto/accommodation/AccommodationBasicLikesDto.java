package bg.exploreBG.model.dto.accommodation;

public record AccommodationBasicLikesDto(
        Long id,
        String accommodationName,
        String imageUrl,
        String nextTo,
        boolean likedByUser
) {
}
