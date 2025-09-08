package bg.exploreBG.service.moderation;

import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.querybuilder.GpxQueryBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class GpxModerationService {
    private final GpxQueryBuilder gpxQueryBuilder;

    public GpxModerationService(GpxQueryBuilder gpxQueryBuilder) {
        this.gpxQueryBuilder = gpxQueryBuilder;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerIdByGpxId(Long gpxId) {
        Long reviewerId = this.gpxQueryBuilder.getReviewerIdByGpxId(gpxId);
        return new UserIdDto(reviewerId);
    }
}
