package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.model.validation.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
@FieldMatch(
        first = "newPassword",
        second = "confirmNewPassword",
        message = "Passwords do no match!"
)
public record UserUpdatePasswordDto(
        @NotBlank(message = "Please enter your password.")
        @Size(min = 5, max = 24, message = "Your password must be between 5 and 24 characters.")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{4,23}$",
                message = "Your password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no spaces."
        )
        String newPassword,
        String confirmNewPassword,

        @NotBlank(message = "Please enter your current password.")
        String currentPassword
) {
}
