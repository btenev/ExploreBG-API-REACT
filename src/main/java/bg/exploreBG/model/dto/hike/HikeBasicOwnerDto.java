package bg.exploreBG.model.dto.hike;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record HikeBasicOwnerDto(
        Long id,
        String hikeName,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime hikeDate,
        String imageUrl
) {
}
