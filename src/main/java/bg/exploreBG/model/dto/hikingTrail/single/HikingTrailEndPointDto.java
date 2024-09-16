package bg.exploreBG.model.dto.hikingTrail.single;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record HikingTrailEndPointDto(
        String endPoint,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
