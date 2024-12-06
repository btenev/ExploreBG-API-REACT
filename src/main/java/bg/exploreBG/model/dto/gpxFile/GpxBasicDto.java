package bg.exploreBG.model.dto.gpxFile;

import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record GpxBasicDto(
        Long id,
        String gpxUrl,
        @JsonSerialize(using = StatusEnumSerializer.class)
        @JsonProperty(value = "gpxStatus")
        StatusEnum status,
        UserBasicInfo reviewedBy
) {
}
