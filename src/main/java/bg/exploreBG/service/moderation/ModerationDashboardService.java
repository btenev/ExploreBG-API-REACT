package bg.exploreBG.service.moderation;

import bg.exploreBG.model.dto.EntitiesPendingApprovalCountDto;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class ModerationDashboardService {
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;

    public ModerationDashboardService(
            AccommodationQueryBuilder accommodationQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            HikingTrailQueryBuilder hikingTrailQueryBuilder
    ) {
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public EntitiesPendingApprovalCountDto getPendingApprovalEntitiesCount() {
        int accommodationCount =
                this.accommodationQueryBuilder
                        .getAccommodationCountByAccommodationStatus(SuperUserReviewStatusEnum.PENDING);
        int destinationCount =
                this.destinationQueryBuilder.getDestinationCountByStatus(SuperUserReviewStatusEnum.PENDING);
        int trailCount =
                this.hikingTrailQueryBuilder.getTrailCountByStatus(SuperUserReviewStatusEnum.PENDING);
        return new EntitiesPendingApprovalCountDto(accommodationCount, destinationCount, trailCount);
    }
}
