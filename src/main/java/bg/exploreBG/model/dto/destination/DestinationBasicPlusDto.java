package bg.exploreBG.model.dto.destination;

public record DestinationBasicPlusDto(
        Long id,
        String destinationName,
        String imageUrl,
        String nextTo
) {
}
