package bg.exploreBG.model.dto.user;

import java.util.List;

public record UserSessionDto(
        Long id,
        String username,
        String imageUrl,
        List<String> roles
) {
}
