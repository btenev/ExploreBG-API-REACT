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
        @NotBlank(message = "Password field cannot be empty!")
        @Size(min = 5, max = 24, message = "Password must be between 5 and 24 characters!")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{4,23}$",
                message = "Password must contain one or more digit from 0 to 9, one or more lowercase letter, one or more uppercase letter, one or more special character, no space."
        )
        String newPassword,
        String confirmNewPassword,
        String current
) {
}
