package bg.exploreBG.web.moderation;

import bg.exploreBG.model.dto.accommodation.AccommodationForApprovalProjection;
import bg.exploreBG.model.dto.accommodation.AccommodationReviewDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateOrReviewDto;
import bg.exploreBG.model.dto.hikingTrail.single.EntitySuperUserReviewStatusDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.service.moderation.AccommodationReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/moderation/accommodations")
public class AccommodationReviewController {
    private static final String ACCOMMODATION_FOLDER = "Accommodations";
    private final AccommodationReviewService accommodationReviewService;

    public AccommodationReviewController(AccommodationReviewService accommodationReviewService) {
        this.accommodationReviewService = accommodationReviewService;
    }

    @GetMapping("/waiting-approval")
    public ResponseEntity<Page<AccommodationForApprovalProjection>> waitingForApprovalAccommodations(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<AccommodationForApprovalProjection> forApproval =
                this.accommodationReviewService.getAllAccommodationForApproval(pageable);

        return ResponseEntity.ok(forApproval);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}/review")
    public ResponseEntity<AccommodationReviewDto> reviewAccommodation(
            @PathVariable("id") Long accommodationId
    ) {
        AccommodationReviewDto toReview =
                this.accommodationReviewService
                        .reviewAccommodation(accommodationId, SuperUserReviewStatusEnum.PENDING);

        return ResponseEntity.ok(toReview);
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<Void> claimTrail(
            @PathVariable("id") Long accommodationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        this.accommodationReviewService.claimAccommodation(accommodationId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unclaim")
    public ResponseEntity<Void> unclaimTrail(
            @PathVariable("id") Long accommodationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        this.accommodationReviewService.unclaimAccommodation(accommodationId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveAccommodation(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationCreateOrReviewDto accommodationCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        SuperUserReviewStatusEnum accommodationStatus =
                this.accommodationReviewService
                        .approveAccommodation(accommodationId, accommodationCreateOrReviewDto, exploreBgUserDetails);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(accommodationStatus));
    }

    @PatchMapping("/{id}/images/claim")
    public ResponseEntity<Void> claimAccommodationImages(
            @PathVariable("id") Long accommodationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.accommodationReviewService
                .claimAccommodationImages(accommodationId, userDetails);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/images/unclaim")
    public ResponseEntity<Void> unclaimAccommodationImages(
            @PathVariable("id") Long accommodationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.accommodationReviewService
                .unclaimAccommodationImages(accommodationId, userDetails);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/images/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveAccommodationImagesClaim(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody ImageApproveDto imageApproveDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuperUserReviewStatusEnum accommodationStatus =
                this.accommodationReviewService
                        .approveAccommodationImages(accommodationId, imageApproveDto, userDetails, ACCOMMODATION_FOLDER);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(accommodationStatus));
    }
}
