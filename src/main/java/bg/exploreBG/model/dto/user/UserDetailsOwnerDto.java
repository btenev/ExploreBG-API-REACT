package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeBasicOwnerDto;

import java.time.LocalDate;
import java.util.List;

public record UserDetailsOwnerDto(
        Long id,
        String username,
        String email,
        String gender,
        LocalDate birthdate,
        String imageUrl,
        String userInfo,
        List<HikeBasicOwnerDto> createdHikes
) {
}
