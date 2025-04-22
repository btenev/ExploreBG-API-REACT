package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.Size;

public record UserUpdateInfo(

        /*NOTE: userInfo is not mandatory field it could be empty  @NotBlank(message = "Please enter your user info.") */
        @Size(max = 1500, message = "Your user info can be a maximum of 1500 characters.")
        String userInfo
) {
}
