package bg.exploreBG.model.dto.gpxFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record GpxUrlDateDto(
        String gpxUrl,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime creationDate
) {
}
