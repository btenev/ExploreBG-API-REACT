package bg.exploreBG.model.dto.accommodation;

import bg.exploreBG.model.dto.image.ImageIdUrlIsMainStatusDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.FoodAvailabilityEnum;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.AccessibilityEnumSerializer;
import bg.exploreBG.serializer.AccommodationTypeEnumSerializer;
import bg.exploreBG.serializer.FoodAvailabilityEnumEnumSerializer;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

public record AccommodationReviewDto(
        Long id,
        String accommodationName,
        UserBasicInfo reviewedBy,
        String phoneNumber,
        String site,
        String accommodationInfo,
        Integer bedCapacity,
        Double pricePerBed,
        @JsonSerialize(using = FoodAvailabilityEnumEnumSerializer.class)
        FoodAvailabilityEnum foodAvailable,
        @JsonSerialize(using = AccessibilityEnumSerializer.class)
        AccessibilityEnum access,
        @JsonSerialize(using = AccommodationTypeEnumSerializer.class)
        AccommodationTypeEnum type,
        String nextTo,
        List<ImageIdUrlIsMainStatusDto> images,
        @JsonSerialize(using = StatusEnumSerializer.class)
        @JsonProperty(value = "detailsStatus")
        StatusEnum status
) {
}
