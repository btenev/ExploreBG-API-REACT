package bg.exploreBG.web.moderation;

import bg.exploreBG.model.dto.destination.DestinationForApprovalProjection;
import bg.exploreBG.model.dto.destination.DestinationReviewDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.dto.hikingTrail.single.EntitySuperUserReviewStatusDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.service.moderation.DestinationReviewService;
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
@RequestMapping("/api/moderation/destinations")
public class DestinationReviewController {
    private static final String DESTINATION_FOLDER = "Destinations";
    private final DestinationReviewService destinationReviewService;

    public DestinationReviewController(DestinationReviewService destinationReviewService) {
        this.destinationReviewService = destinationReviewService;
    }

    @GetMapping("/waiting-approval")
    public ResponseEntity<Page<DestinationForApprovalProjection>> waitingForApprovalDestinations(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<DestinationForApprovalProjection> forApproval =
                this.destinationReviewService.getAllDestinationForApproval(pageable);

        return ResponseEntity.ok(forApproval);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}/review")
    public ResponseEntity<DestinationReviewDto> reviewDestination(
            @PathVariable("id") Long destinationId
    ) {
        DestinationReviewDto toReview =
                this.destinationReviewService
                        .reviewDestination(destinationId, SuperUserReviewStatusEnum.PENDING);

        return ResponseEntity.ok(toReview);
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<Void> claimDestination(
            @PathVariable("id") Long destinationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        this.destinationReviewService.claimDestination(destinationId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unclaim")
    public ResponseEntity<Void> unclaimDestination(
            @PathVariable("id") Long destinationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        this.destinationReviewService.unclaimDestination(destinationId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveDestination(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationCreateOrReviewDto destinationCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        SuperUserReviewStatusEnum destinationStatus =
                this.destinationReviewService
                        .approveDestination(destinationId, destinationCreateOrReviewDto, exploreBgUserDetails);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(destinationStatus));
    }

    @PatchMapping("/{id}/images/claim")
    public ResponseEntity<Void> claimDestinationImages(
            @PathVariable("id") Long destinationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.destinationReviewService
                .claimDestinationImages(destinationId, userDetails);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/images/unclaim")
    public ResponseEntity<Void> unclaimDestinationImages(
            @PathVariable("id") Long destinationId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.destinationReviewService
                .unclaimDestinationImages(destinationId, userDetails);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/images/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveDestinationImagesClaim(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody ImageApproveDto imageApproveDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuperUserReviewStatusEnum destinationStatus =
                this.destinationReviewService
                        .approveDestinationImages(destinationId, imageApproveDto, userDetails, DESTINATION_FOLDER);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(destinationStatus));
    }
}
