package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.NotBlank;

public record UpdateGenderDto(
        @NotBlank(message = "Field gender can not be blank!")
        String gender
) {
}
