package bg.exploreBG.web.moderation;

import bg.exploreBG.model.dto.EntitiesPendingApprovalCountDto;
import bg.exploreBG.service.moderation.ModerationDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/moderation/dashboard")
public class ModerationDashboardController {
    private final ModerationDashboardService moderationDashboardService;

    public ModerationDashboardController(ModerationDashboardService moderationDashboardService) {
        this.moderationDashboardService = moderationDashboardService;
    }

    @GetMapping("/entities/waiting-approval/count")
    public ResponseEntity<EntitiesPendingApprovalCountDto> getPendingApprovalEntitiesCount() {

        EntitiesPendingApprovalCountDto entitiesCount =
                this.moderationDashboardService.getPendingApprovalEntitiesCount();

        return ResponseEntity.ok(entitiesCount);
    }
}
