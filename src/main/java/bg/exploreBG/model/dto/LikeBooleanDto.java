package bg.exploreBG.model.dto;

import jakarta.validation.constraints.NotNull;

public record LikeBooleanDto(
        @NotNull(message = "Field can not be empty!")
        Boolean like
) {
}
