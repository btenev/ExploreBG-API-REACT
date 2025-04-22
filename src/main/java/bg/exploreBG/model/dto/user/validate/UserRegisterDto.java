package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.model.validation.FieldMatch;
import bg.exploreBG.model.validation.UniqueUserEmail;
import bg.exploreBG.model.validation.UniqueUsername;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@FieldMatch(
        first = "password",
        second = "confirmPassword",
        message = "Your passwords do no match!"
)
public record UserRegisterDto(
        @NotBlank(message = "Please enter your email address.")
        @Email(message = "The email format is incorrect.", regexp = "[a-z0-9._+-]+@[a-z0-9.-]+\\.[a-z]{2,4}")
        @UniqueUserEmail(message = "This email address is already taken.")
        String email,

        @NotBlank(message = "Please enter your username.")
        @Size(min = 3, max = 30, message = "Your username must be between 3 and 30 characters.")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9_]{2,29}$",
                message = "Your username must start with a letter and can include letters, numbers, and underscores."
        )
        @UniqueUsername(message = "This username is already taken.")
        String username,

        @NotBlank(message = "Please enter your password.")
        @Size(min = 5, max = 24, message = "Your password must be between 5 and 24 characters.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{4,23}$",
                message = "Your password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no spaces."
        )
        String password,
        String confirmPassword
) {
}
