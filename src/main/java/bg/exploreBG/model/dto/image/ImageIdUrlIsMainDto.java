package bg.exploreBG.model.dto.image;

public record ImageIdUrlIsMainDto(
        Long id,
        String imageUrl,
        boolean isMain
) {
}
