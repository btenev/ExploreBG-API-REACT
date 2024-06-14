package bg.exploreBG.model.dto.user;

public record UserIdNameEmailDto(
        Long id,
        String email,
        String username
) {
}
