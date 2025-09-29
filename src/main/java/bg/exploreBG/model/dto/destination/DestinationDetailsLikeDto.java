package bg.exploreBG.model.dto.destination;

import bg.exploreBG.model.dto.image.ImageIdUrlIsMainStatusDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.DestinationTypeEnum;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.DestinationTypeEnumSerializer;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;

public record DestinationDetailsLikeDto(
        Long id,
        String destinationName,
        Double latitude,
        Double longitude,
        String destinationInfo,
        String nextTo,
        UserBasicInfo createdBy,
        @JsonSerialize(using = DestinationTypeEnumSerializer.class)
        DestinationTypeEnum type,
        List<ImageIdUrlIsMainStatusDto> images,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate,
        @JsonSerialize(using = StatusEnumSerializer.class)
        @JsonProperty(value = "detailsStatus")
        StatusEnum status,
        boolean likedByUser
) {
}
