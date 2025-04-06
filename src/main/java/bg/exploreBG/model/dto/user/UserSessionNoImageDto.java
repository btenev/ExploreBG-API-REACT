package bg.exploreBG.model.dto.user;

import java.util.List;

public record UserSessionNoImageDto(
        Long id,
        String username,
        String imageUrl,
        List<String> roles) {
}
