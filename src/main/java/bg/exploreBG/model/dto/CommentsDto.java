package bg.exploreBG.model.dto;

public record CommentsDto(Long id,
                          String message,
                          UserBasicInfo owner) {
}
