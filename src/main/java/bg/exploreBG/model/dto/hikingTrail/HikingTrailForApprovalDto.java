package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.enums.StatusEnum;

import java.time.LocalDateTime;

public record HikingTrailForApprovalDto(
        Long id,
        String name,
        StatusEnum status,
        LocalDateTime creationDate,
        String reviewedBy
) {
}
