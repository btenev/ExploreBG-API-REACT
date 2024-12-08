package bg.exploreBG.model.dto.accommodation;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record AccommodationWrapperDto(
        List<AccommodationIdAndAccommodationName> availableHuts,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
