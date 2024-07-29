package bg.exploreBG.model.dto.user;

import java.util.List;

public record UserEmailRolesDto(
        String email,
        List<String> roles
) {
}
