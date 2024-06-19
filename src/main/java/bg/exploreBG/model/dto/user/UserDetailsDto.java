package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.hike.HikeBasicDto;

import java.time.LocalDate;
import java.util.List;

public record UserDetailsDto(
        Long id,
        String username,
        String email,
        String gender,
        LocalDate birthdate,
        String imageUrl,
        String userInfo,
        List<HikeBasicDto> createdHikes
) {
}
