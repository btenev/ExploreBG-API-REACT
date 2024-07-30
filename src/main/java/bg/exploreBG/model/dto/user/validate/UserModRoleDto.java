package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.NotNull;

public record UserModRoleDto(
        @NotNull
        Boolean moderator
) {
}
