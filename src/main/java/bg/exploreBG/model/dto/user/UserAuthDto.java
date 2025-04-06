package bg.exploreBG.model.dto.user;

import java.util.List;

public record UserAuthDto(
        Long id,
        String email,
        String username,
        String imageUrl,
        List<String> roles
){}

