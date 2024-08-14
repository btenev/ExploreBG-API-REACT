package bg.exploreBG.model.dto;

public record EntitiesForApprovalUnderReviewCountDto(
        int accommodationsPending,
        int accommodationsReview,
        int destinationsPending,
        int destinationsReview,
        int trailsPending,
        int trailsReview
) {
}
