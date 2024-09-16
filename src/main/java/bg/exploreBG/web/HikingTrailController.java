package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.single.CommentDeletedReplyDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.service.HikingTrailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trails")
public class HikingTrailController {
    private static final Logger logger = LoggerFactory.getLogger(HikingTrailController.class);
    private final HikingTrailService hikingTrailService;

    public HikingTrailController(HikingTrailService hikingTrailService) {
        this.hikingTrailService = hikingTrailService;
    }

    /*
    APPROVED
    */
    @GetMapping("/random")
    public ResponseEntity<?> getFourRandomHikingTrails(
            Authentication authentication
    ) {
        List<?> randomTrails;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                randomTrails = this.hikingTrailService.getRandomNumOfHikingTrailsWithLikes(4, userDetails);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            randomTrails = this.hikingTrailService.getRandomNumOfHikingTrails(4);
        }

        ApiResponse<?> response = new ApiResponse<>(randomTrails);

        return ResponseEntity.ok(response);
    }

    /*
    APPROVED
    @Transactional for the time being, more information in the data query
    */
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getHikingTrail(
            @PathVariable("id") Long trailId,
            Authentication authentication
    ) {
        ApiResponse<?> response;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                response = new ApiResponse<>(this.hikingTrailService.getHikingTrailAuthenticated(trailId, userDetails));
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            response = new ApiResponse<>(this.hikingTrailService.getHikingTrail(trailId));
        }

        return ResponseEntity.ok(response);
    }

    /*
    APPROVED
    */
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir,
            @RequestParam(value = "sortByLikedUser", required = false) Boolean sortByLikedUser,
            Authentication authentication
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);
        Page<?> allHikingTrails;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                allHikingTrails = this.hikingTrailService
                        .getAllHikingTrailsWithLikes(userDetails, pageable, sortByLikedUser);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            allHikingTrails = this.hikingTrailService.getAllHikingTrails(pageable);
        }

        ApiResponse<Page<?>> response = new ApiResponse<>(allHikingTrails);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<HikingTrailIdDto>> createHikingTrail(
            @Valid @RequestBody HikingTrailCreateOrReviewDto hikingTrailCreateOrReviewDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        logger.debug("Display create hiking trail request {}", hikingTrailCreateOrReviewDto);

        Long newHikingTrailId =
                this.hikingTrailService.createHikingTrail(hikingTrailCreateOrReviewDto, userDetails);

        HikingTrailIdDto hikingTrailIdDto = new HikingTrailIdDto(newHikingTrailId);

        ApiResponse<HikingTrailIdDto> response = new ApiResponse<>(hikingTrailIdDto);

        return ResponseEntity
                .created(URI.create("api/trails/" + newHikingTrailId))
                .body(response);
    }

//    @DeleteMapping("/{id}")
//    ResponseEntity<?> deleteHikingTrail(@PathVariable("id") Long trailId) {
//        boolean success = this.hikingTrailService.deleteHikingTrail(trailId);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/{id}/reviewer")
    public ResponseEntity<UserIdDto> getHikingTrailReviewer(
            @PathVariable("id") Long trailId
    ) {
        UserIdDto reviewerId = this.hikingTrailService.getReviewerId(trailId);

        return ResponseEntity.ok(reviewerId);
    }

    @PatchMapping("/{id}/start-point")
    public ResponseEntity<ApiResponse<HikingTrailStartPointDto>> updateStartPoint(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateStartPointDto hikingTrailUpdateStartPointDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailStartPointDto hikingTrailStartPointDto =
                this.hikingTrailService
                        .updateHikingTrailStartPoint(trailId, hikingTrailUpdateStartPointDto, userDetails);

        ApiResponse<HikingTrailStartPointDto> response = new ApiResponse<>(hikingTrailStartPointDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/end-point")
    public ResponseEntity<ApiResponse<HikingTrailEndPointDto>> updateEndPoint(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateEndPointDto hikingTrailUpdateEndPointDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailEndPointDto hikingTrailEndPointDto =
                this.hikingTrailService
                        .updateHikingTrailEndPoint(trailId, hikingTrailUpdateEndPointDto, userDetails);

        ApiResponse<HikingTrailEndPointDto> response = new ApiResponse<>(hikingTrailEndPointDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/total-distance")
    public ResponseEntity<ApiResponse<HikingTrailTotalDistanceDto>> updateTotalDistance(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTotalDistanceDto hikingTrailUpdateTotalDistanceDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        logger.debug("Display hikingTrailUpdateTotalDistance {}", hikingTrailUpdateTotalDistanceDto);

        HikingTrailTotalDistanceDto hikingTrailTotalDistanceDto =
                this.hikingTrailService
                        .updateHikingTrailTotalDistance(trailId, hikingTrailUpdateTotalDistanceDto, userDetails);

        ApiResponse<HikingTrailTotalDistanceDto> response = new ApiResponse<>(hikingTrailTotalDistanceDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/water-available")
    public ResponseEntity<ApiResponse<HikingTrailWaterAvailableDto>> updateWaterAvailable(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateWaterAvailableDto hikingTrailUpdateWaterAvailableDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailWaterAvailableDto hikingTrailWaterAvailableDto =
                this.hikingTrailService
                        .updateHikingTrailWaterAvailable(trailId, hikingTrailUpdateWaterAvailableDto, userDetails);

        ApiResponse<HikingTrailWaterAvailableDto> response = new ApiResponse<>(hikingTrailWaterAvailableDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/activity")
    public ResponseEntity<ApiResponse<HikingTrailActivityDto>> updateHikingTrailActivity(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateActivityDto hikingTrailUpdateActivityDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailActivityDto hikingTrailActivityDto =
                this.hikingTrailService.updateHikingTrailActivity(trailId, hikingTrailUpdateActivityDto, userDetails);

        ApiResponse<HikingTrailActivityDto> response = new ApiResponse<>(hikingTrailActivityDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/trail-info")
    public ResponseEntity<ApiResponse<HikingTrailTrailInfoDto>> updateTrailInfo(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTrailInfoDto hikingTrailUpdateTrailInfoDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailTrailInfoDto hikingTrailTrailInfoDto =
                this.hikingTrailService
                        .updateHikingTrailTrailInfo(trailId, hikingTrailUpdateTrailInfoDto, userDetails);

        ApiResponse<HikingTrailTrailInfoDto> response = new ApiResponse<>(hikingTrailTrailInfoDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/elevation-gained")
    public ResponseEntity<ApiResponse<HikingTrailElevationGainedDto>> updateElevationGained(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateElevationGainedDto hikingTrailUpdateElevationGainedDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailElevationGainedDto hikingTrailElevationGainedDto =
                this.hikingTrailService
                        .updateHikingTrailElevationGained(trailId, hikingTrailUpdateElevationGainedDto, userDetails);

        ApiResponse<HikingTrailElevationGainedDto> response = new ApiResponse<>(hikingTrailElevationGainedDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/trail-difficulty")
    public ResponseEntity<ApiResponse<HikingTrailDifficultyDto>> updateTrailDifficulty(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateTrailDifficultyDto hikingTrailUpdateTrailDifficultyDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailDifficultyDto trailDifficultyDto =
                this.hikingTrailService
                        .updateHikingTrailDifficulty(trailId, hikingTrailUpdateTrailDifficultyDto, userDetails);

        ApiResponse<HikingTrailDifficultyDto> response = new ApiResponse<>(trailDifficultyDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/available-huts")
    public ResponseEntity<ApiResponse<List<AccommodationBasicDto>>> updateAvailableHuts(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateAvailableHutsDto hikingTrailUpdateAvailableHutsDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<AccommodationBasicDto> accommodationBasicDto =
                this.hikingTrailService
                        .updateHikingTrailAvailableHuts(trailId, hikingTrailUpdateAvailableHutsDto, userDetails);

        ApiResponse<List<AccommodationBasicDto>> response = new ApiResponse<>(accommodationBasicDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/destinations")
    public ResponseEntity<ApiResponse<List<DestinationBasicDto>>> updateDestinations(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateDestinationsDto hikingTrailUpdateDestinationsDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<DestinationBasicDto> destinationBasicDto =
                this.hikingTrailService
                        .updateHikingTrailDestinations(trailId, hikingTrailUpdateDestinationsDto, userDetails);

        ApiResponse<List<DestinationBasicDto>> response = new ApiResponse<>(destinationBasicDto);

        return ResponseEntity.ok(response);
    }

    /*TODO: Discuss validation message with Ivo*/
    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ApiResponse<Boolean>> changeMainImage(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean updatedMainImage =
                this.hikingTrailService.updateHikingTrailMainImage(trailId, imageMainUpdateDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(updatedMainImage);

        return ResponseEntity.ok(response);
    }

    /*TODO: returns only messages, no errors*/
    @PatchMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleHikingTrailLike(
            @PathVariable("id") Long trailId,
            @RequestBody LikeBooleanDto likeBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.hikingTrailService.likeOrUnlikeTrail(trailId, likeBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    /*
    APPROVED
    */
    @GetMapping("/select")
    public ResponseEntity<List<HikingTrailIdTrailNameDto>> select() {
        List<HikingTrailIdTrailNameDto> selected = this.hikingTrailService.selectAll();

        return ResponseEntity.ok(selected);
    }

    @PostMapping("/{trailId}/comments")
    public ResponseEntity<ApiResponse<CommentDto>> createTrailComment(
            @PathVariable Long trailId,
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto commentDto = this.hikingTrailService
                .addNewTrailComment(trailId, commentCreateDto, userDetails);

        ApiResponse<CommentDto> response = new ApiResponse<>(commentDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{trailId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentDeletedReplyDto>> deleteTrailComment(
            @PathVariable Long trailId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean removed = this.hikingTrailService.deleteTrailComment(trailId, commentId, userDetails);

        CommentDeletedReplyDto replyDto = new CommentDeletedReplyDto(removed);

        ApiResponse<CommentDeletedReplyDto> response = new ApiResponse<>(replyDto);

        return ResponseEntity.ok(response);
    }
}
