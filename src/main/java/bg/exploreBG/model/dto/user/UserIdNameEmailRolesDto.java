package bg.exploreBG.model.dto.user;

import java.util.List;

public record UserIdNameEmailRolesDto(
        Long id,
        String email,
        String username,
        List<String> roles
) {
}
