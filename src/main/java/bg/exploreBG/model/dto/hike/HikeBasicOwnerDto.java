package bg.exploreBG.model.dto.hike;

import java.time.LocalDate;

public record HikeBasicOwnerDto(
        Long id,
        String hikeName,
        LocalDate hikeDate,
        String imageUrl
) {
}
