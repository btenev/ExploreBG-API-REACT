package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.image.ImageProjections;
import bg.exploreBG.model.dto.user.UserIdNameProjection;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

public interface HikingTrailForApprovalProjection {
    Long getId();
    @Value("#{target.startPoint + ' - ' + target.endPoint}")
    String getName();
    @JsonSerialize(using = StatusEnumSerializer.class)
    @JsonProperty(value = "details_status")
    StatusEnum getStatus();
    LocalDateTime getCreationDate();
    UserIdNameProjection getReviewedBy();
    List<ImageProjections> getImages();
}
