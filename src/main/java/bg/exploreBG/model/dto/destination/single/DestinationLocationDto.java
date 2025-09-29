package bg.exploreBG.model.dto.destination.single;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record DestinationLocationDto(
        Double longitude,
        Double latitude,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
