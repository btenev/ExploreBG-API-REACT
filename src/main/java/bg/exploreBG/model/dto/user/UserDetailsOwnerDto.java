package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.hike.HikeBasicOwnerDto;
import bg.exploreBG.model.enums.GenderEnum;
import bg.exploreBG.serializer.GenderEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
import java.util.List;

public record UserDetailsOwnerDto(
        Long id,
        String username,
        String email,
        @JsonSerialize(using = GenderEnumSerializer.class)
        GenderEnum gender,
        LocalDate birthdate,
        String imageUrl,
        String userInfo,
        List<HikeBasicOwnerDto> createdHikes
) {
}
