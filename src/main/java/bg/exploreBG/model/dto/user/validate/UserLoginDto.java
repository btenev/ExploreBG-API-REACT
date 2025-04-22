package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginDto(
        @NotBlank(message = "Please enter your email address.")
        @Email(message = "The email format is incorrect.", regexp = "[a-z0-9._+-]+@[a-z0-9.-]+\\.[a-z]{2,4}")
        String email,


        @NotBlank(message = "Please enter your password.")
        @Size(min = 5, max = 24, message = "Your password must be between 5 and 24 characters.")
        String password
) {
}
