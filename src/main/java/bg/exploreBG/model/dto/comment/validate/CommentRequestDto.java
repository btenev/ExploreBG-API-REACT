package bg.exploreBG.model.dto.comment.validate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequestDto(
        @NotBlank(message = "Please enter a comment.")
        @Size(
                max = 250,
                message = "Comment must not exceed {max} characters."
        )
        String message
) {
}
