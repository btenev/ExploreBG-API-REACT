package bg.exploreBG.model.dto.comment.validate;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateDto(
        @NotBlank(message = "Comment message can not be empty!")
        String message
) {
}
