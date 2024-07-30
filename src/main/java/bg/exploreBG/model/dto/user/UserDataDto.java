package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.role.RoleDto;

import java.time.LocalDateTime;
import java.util.List;

public record UserDataDto(
        Long id,
        String username,
        String imageUrl,
        LocalDateTime creationDate,
        List<RoleDto> roles
) {
}
