package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.model.validation.UniqueUsername;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateUsernameDto(
        @NotBlank(message = "Please, enter your username!")
        @Size(min = 3, max = 30, message = "Username length should be between 3 and 30 characters!")
        @Pattern(
                regexp = "^[A-Za-z][A-Za-z0-9_]{2,29}$",
                message = "Username should start with A-Z or a-z. All other characters can be letters(upper or lower case), numbers or an underscore!"
        )
        @UniqueUsername(message = "Username already exist!")
                @JsonProperty
        String username
) {
}
