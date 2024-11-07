package bg.exploreBG.model.dto.gpxFile;

import bg.exploreBG.model.dto.user.UserIdNameProjection;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface GpxFileUrlProjection {
    String getGpxUrl();

    @JsonSerialize(using = StatusEnumSerializer.class)
    @JsonProperty(value = "gpxStatus")
    StatusEnum getStatus();

    UserIdNameProjection getReviewedBy();
}
