package bg.exploreBG.model.dto.comment.validate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateDto(
        @NotBlank(message = "err-comment-required")
        @Size(
                max = 250,
                message = "err-comment-max-length"
        )
        String message
) {
}
