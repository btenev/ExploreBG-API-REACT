package bg.exploreBG.model.dto.user;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.enums.GenderEnum;
import bg.exploreBG.serializer.GenderEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDate;
import java.util.List;

public record UserDetailsDto(
        Long id,
        String username,
        String email,
        @JsonSerialize(using = GenderEnumSerializer.class)
        GenderEnum gender,
        LocalDate birthdate,
        String imageUrl,
        String userInfo,
        List<HikeBasicDto> createdHikes,
        List<HikingTrailBasicDto> createdTrails
) {
}
