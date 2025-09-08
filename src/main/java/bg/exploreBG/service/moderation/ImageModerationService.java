package bg.exploreBG.service.moderation;

import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.querybuilder.ImageQueryBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ImageModerationService {
    private final ImageQueryBuilder imageQueryBuilder;

    public ImageModerationService(ImageQueryBuilder imageQueryBuilder) {
        this.imageQueryBuilder = imageQueryBuilder;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerIdByImageId(Long imageId) {
        Long reviewerId = this.imageQueryBuilder.getReviewerIdByImageId(imageId);
        return new UserIdDto(reviewerId);
    }
}
