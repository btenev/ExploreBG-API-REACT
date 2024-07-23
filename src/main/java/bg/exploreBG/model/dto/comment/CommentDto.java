package bg.exploreBG.model.dto.comment;

import bg.exploreBG.model.dto.user.UserBasicInfo;

public record CommentDto(
        Long id,
        String message,
        UserBasicInfo owner
) {
}
