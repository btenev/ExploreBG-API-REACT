package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.dto.user.UserIdNameProjection;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface HikingTrailForApprovalProjection {
    Long getId();
    @Value("#{target.startPoint + ' - ' + target.endPoint}")
    String getName();
    @JsonSerialize(using = StatusEnumSerializer.class)
    StatusEnum getTrailStatus();
    LocalDateTime getCreationDate();
    UserIdNameProjection getReviewedBy();
}
