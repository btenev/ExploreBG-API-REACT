package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.serializer.StatusEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.time.LocalDateTime;

public record HikingTrailForApprovalDto(
        Long id,
        String name,
        @JsonSerialize(using = StatusEnumSerializer.class)
        StatusEnum status,
        LocalDateTime creationDate,
        String reviewedBy
) {
}
