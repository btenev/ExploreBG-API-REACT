package bg.exploreBG.model.dto.image;

import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record ImageIdUrlIsMainStatusDto(
        Long id,
        String imageUrl,
        boolean isMain,
        @JsonProperty(value = "imageStatus")
        @JsonSerialize(using = StatusEnumSerializer.class)
        StatusEnum status
) {
}
