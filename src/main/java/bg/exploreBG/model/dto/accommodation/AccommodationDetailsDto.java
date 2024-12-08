package bg.exploreBG.model.dto.accommodation;

import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainStatusDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.AccessibilityEnumSerializer;
import bg.exploreBG.serializer.AccommodationTypeEnumSerializer;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;

public record AccommodationDetailsDto(
        Long id,
        String accommodationName,
        UserBasicInfo createdBy,
        String phoneNumber,
        String site,
        String accommodationInfo,
        Integer bedCapacity,
        Double pricePerBed,
        Boolean foodAvailable,
        @JsonSerialize(using = AccessibilityEnumSerializer.class)
        AccessibilityEnum access,
        @JsonSerialize(using = AccommodationTypeEnumSerializer.class)
        AccommodationTypeEnum type,
        String nextTo,
        List<ImageIdUrlIsMainStatusDto> images,
        List<CommentDto> comments,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate,
        @JsonSerialize(using = StatusEnumSerializer.class)
        @JsonProperty(value = "detailsStatus")
        StatusEnum status
) {
}
