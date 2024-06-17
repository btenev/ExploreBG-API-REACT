package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.model.validation.UniqueUserEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateEmailDto(
        @NotBlank(message = "Please, enter your email!")
        @Email(message = "User email should be valid!", regexp = "[a-z0-9._+-]+@[a-z0-9.-]+\\.[a-z]{2,4}")
        @UniqueUserEmail(message = "User email already exist!")
        String email
) {
}
