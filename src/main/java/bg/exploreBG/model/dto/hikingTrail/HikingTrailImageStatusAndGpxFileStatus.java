package bg.exploreBG.model.dto.hikingTrail;

import bg.exploreBG.model.enums.StatusEnum;

public record HikingTrailImageStatusAndGpxFileStatus(
        String imageStatus,
        StatusEnum gpxFileStatus
) {

}
