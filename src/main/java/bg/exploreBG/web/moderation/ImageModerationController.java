package bg.exploreBG.web.moderation;

import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.service.moderation.ImageModerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/moderation/images")
public class ImageModerationController {
    private final ImageModerationService imageModerationService;

    public ImageModerationController(ImageModerationService imageModerationService) {
        this.imageModerationService = imageModerationService;
    }

    @GetMapping("/{id}/reviewer")
    public ResponseEntity<UserIdDto> getImageReviewer(
            @PathVariable("id") Long imageId
    ) {
        UserIdDto reviewerId = this.imageModerationService.getReviewerIdByImageId(imageId);

        return ResponseEntity.ok(reviewerId);
    }
}
