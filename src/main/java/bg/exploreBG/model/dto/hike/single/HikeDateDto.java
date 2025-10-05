package bg.exploreBG.model.dto.hike.single;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record HikeDateDto(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime hikeDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
