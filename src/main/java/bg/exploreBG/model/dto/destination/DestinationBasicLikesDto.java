package bg.exploreBG.model.dto.destination;

public record DestinationBasicLikesDto(
        Long id,
        String destinationName,
        String imageUrl,
        String nextTo,
        boolean likedByUser
) {
}
