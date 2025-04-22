package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.model.validation.UniqueUserEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateEmailDto(
        @NotBlank(message = "Please enter your email address.")
        @Email(message = "The email format is incorrect.", regexp = "[a-z0-9._+-]+@[a-z0-9.-]+\\.[a-z]{2,4}")
        @UniqueUserEmail(message = "This email address is already taken.")
        String email
) {
}
