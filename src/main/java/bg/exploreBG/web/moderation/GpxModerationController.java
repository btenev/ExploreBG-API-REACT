package bg.exploreBG.web.moderation;

import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.service.moderation.GpxModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moderation/gpx")
public class GpxModerationController {
    private final GpxModerationService gpxModerationService;

    public GpxModerationController(GpxModerationService gpxModerationService) {
        this.gpxModerationService = gpxModerationService;
    }

    @GetMapping("/{id}/reviewer")
    public ResponseEntity<UserIdDto> getGpxReviewer(
            @PathVariable("id") Long gpxId
    ) {
        UserIdDto reviewerId = this.gpxModerationService.getReviewerIdByGpxId(gpxId);

        return ResponseEntity.ok(reviewerId);
    }
}
