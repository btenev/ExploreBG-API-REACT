package bg.exploreBG.model.dto.destination;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record DestinationWrapperDto(
        List<DestinationIdAndDestinationNameDto> destinations,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
