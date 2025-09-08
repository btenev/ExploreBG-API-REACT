package bg.exploreBG.web.moderation;

import bg.exploreBG.model.dto.gpxFile.validate.GpxApproveDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.single.EntitySuperUserReviewStatusDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.service.moderation.TrailReviewService;
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
@RequestMapping("/api/moderation/trails")
public class TrailReviewController {
    private static final String TRAIL_FOLDER = "Trails";
    private final TrailReviewService trailReviewService;

    public TrailReviewController(TrailReviewService trailReviewService) {
        this.trailReviewService = trailReviewService;
    }

    @GetMapping("/waiting-approval")
    public ResponseEntity<Page<HikingTrailForApprovalProjection>> waitingForApprovalTrails(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<HikingTrailForApprovalProjection> forApproval =
                this.trailReviewService.getAllHikingTrailsForApproval(pageable);

        return ResponseEntity.ok(forApproval);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}/review")
    public ResponseEntity<HikingTrailReviewDto> reviewTrail(
            @PathVariable("id") Long trailId
    ) {
        HikingTrailReviewDto toReview =
                this.trailReviewService.reviewTrail(trailId, SuperUserReviewStatusEnum.PENDING);

        return ResponseEntity.ok(toReview);
    }

    @GetMapping("/{id}/reviewer")
    public ResponseEntity<UserIdDto> getHikingTrailReviewer(
            @PathVariable("id") Long trailId
    ) {
        UserIdDto reviewerId = this.trailReviewService.getReviewerIdByTrailId(trailId);
        return ResponseEntity.ok(reviewerId);
    }

    @PatchMapping("/{id}/claim")
    public ResponseEntity<Void> claimTrail(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.trailReviewService.claimTrail(trailId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/unclaim")
    public ResponseEntity<Void> unclaimTrail(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.trailReviewService.unclaimTrail(trailId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/{id}/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveTrail(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailCreateOrReviewDto trailCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        SuperUserReviewStatusEnum trailStatus =
                this.trailReviewService.approveTrail(trailId, trailCreateOrReviewDto, exploreBgUserDetails);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(trailStatus));
    }

    @PatchMapping("/{id}/images/claim")
    public ResponseEntity<Void> claimTrailImages(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.trailReviewService.claimTrailImages(trailId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/images/unclaim")
    public ResponseEntity<Void> unclaimTrailImages(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.trailReviewService.unclaimTrailImages(trailId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/images/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveTrailImagesClaim(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody ImageApproveDto imageApproveDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuperUserReviewStatusEnum trailStatus =
                this.trailReviewService.approveTrailImages(trailId, imageApproveDto, userDetails, TRAIL_FOLDER);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(trailStatus));
    }

    @PatchMapping("/{id}/gpx-file/claim")
    public ResponseEntity<Void> claimTrailGpxFile(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.trailReviewService.claimTrailGpxFile(trailId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/gpx-file/unclaim")
    public ResponseEntity<Void> unclaimTrailGpxFile(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.trailReviewService.unclaimTrailGpxFile(trailId, userDetails);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/gpx-file/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveTrailGpxFileClaim(
            @PathVariable("id") Long trailId,
            @RequestBody GpxApproveDto gpxApproveDto,
            @AuthenticationPrincipal ExploreBgUserDetails userDetails
    ) {
        SuperUserReviewStatusEnum trailStatus =
                this.trailReviewService.approveTrailGpxFile(trailId, gpxApproveDto, userDetails);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(trailStatus));
    }
}
