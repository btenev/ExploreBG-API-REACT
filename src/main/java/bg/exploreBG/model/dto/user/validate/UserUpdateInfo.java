package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateInfo(
        @NotBlank(message = "Field userInfo can not be blank!")
        String userInfo
) {
}
