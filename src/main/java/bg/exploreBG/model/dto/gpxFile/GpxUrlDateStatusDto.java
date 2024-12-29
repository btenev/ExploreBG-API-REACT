package bg.exploreBG.model.dto.gpxFile;

import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public record GpxUrlDateStatusDto(
        String gpxUrl,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime creationDate,
        @JsonSerialize(using = StatusEnumSerializer.class)
        @JsonProperty(value = "gpxStatus")
        StatusEnum status
) {
}
