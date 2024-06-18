package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserInfo(
        @NotBlank(message = "Field userInfo can not be blank!")
        String userInfo
) {
}
