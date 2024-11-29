package bg.exploreBG.model.dto;

import bg.exploreBG.model.dto.accommodation.AccommodationApprovalReviewCountDto;
import bg.exploreBG.model.dto.accommodation.DestinationApprovalReviewCountDto;
import bg.exploreBG.model.dto.hikingTrail.TrailApprovalReviewCountDto;

public record EntitiesForApprovalUnderReviewCountDto(
        AccommodationApprovalReviewCountDto accommodations,
        int destinations,
        int trails
) {
}
