package bg.exploreBG.model.dto.user.single;

import bg.exploreBG.model.validation.UniqueUsername;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUsernameDto(
        String username
) {
}
