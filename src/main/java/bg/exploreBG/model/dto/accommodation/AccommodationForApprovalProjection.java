package bg.exploreBG.model.dto.accommodation;

import bg.exploreBG.model.dto.image.ImageProjections;
import bg.exploreBG.model.dto.user.UserIdNameProjection;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;
import java.util.List;

public interface AccommodationForApprovalProjection {
    Long getId();

    String getAccommodationName();

    @JsonSerialize(using = StatusEnumSerializer.class)
    @JsonProperty(value = "detailsStatus")
    StatusEnum getStatus();

    LocalDateTime getCreationDate();

    UserIdNameProjection getReviewedBy();

    List<ImageProjections> getImages();
}
