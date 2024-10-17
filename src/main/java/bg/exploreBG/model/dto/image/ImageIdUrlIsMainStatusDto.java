package bg.exploreBG.model.dto.image;

import bg.exploreBG.model.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ImageIdUrlIsMainStatusDto(
        Long id,
        String imageUrl,
        boolean isMain,
        @JsonProperty(value = "imageStatus")
        StatusEnum status
) {
}
