package bg.exploreBG.model.dto;

import bg.exploreBG.model.dto.user.UserBasicInfo;

public record CommentsDto(
        Long id,
        String message,
        UserBasicInfo owner
) {
}
