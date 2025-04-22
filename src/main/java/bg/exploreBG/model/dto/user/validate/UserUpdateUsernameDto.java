package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.model.validation.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateUsernameDto(
        @NotBlank(message = "Please enter your username.")
        @Size(min = 3, max = 30, message = "Your username must be between 3 and 30 characters.")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9_]{2,29}$",
                message = "Your username must start with a letter and can include letters, numbers, and underscores."
        )
        @UniqueUsername(message = "This username is already taken.")
        String username
) {
}
