package bg.exploreBG.model.dto.destination;

import bg.exploreBG.model.dto.image.ImageIdUrlIsMainStatusDto;
import bg.exploreBG.model.enums.DestinationTypeEnum;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.DestinationTypeEnumSerializer;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public record DestinationReviewDto(
        Long id,
        String destinationName,
        Double latitude,
        Double longitude,
        String destinationInfo,
        String nextTo,
        @JsonSerialize(using = DestinationTypeEnumSerializer.class)
        DestinationTypeEnum type,
        List<ImageIdUrlIsMainStatusDto> images,
        @JsonSerialize(using = StatusEnumSerializer.class)
        @JsonProperty(value = "detailsStatus")
        StatusEnum status
) {
}
