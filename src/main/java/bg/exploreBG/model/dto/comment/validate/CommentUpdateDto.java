package bg.exploreBG.model.dto.comment.validate;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateDto(
        @NotBlank(message = "Update message can not be empty!")
        String message
) {
}
