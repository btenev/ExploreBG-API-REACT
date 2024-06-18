package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.NotBlank;

public record UpdateGenderDto(
        @NotBlank(message = "be-valid-err-gender")
        String gender
) {
}
