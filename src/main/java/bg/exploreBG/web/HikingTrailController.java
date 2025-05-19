package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.LikeResponseDto;
import bg.exploreBG.model.dto.accommodation.AccommodationWrapperDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.single.CommentDeletedReplyDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationWrapperDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.service.HikingTrailService;
import bg.exploreBG.service.HikingTrailUpdateService;
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
    private final HikingTrailUpdateService hikingTrailUpdateService;

    public HikingTrailController(
            HikingTrailService hikingTrailService,
            HikingTrailUpdateService hikingTrailUpdateService
    ) {
        this.hikingTrailService = hikingTrailService;
        this.hikingTrailUpdateService = hikingTrailUpdateService;
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

//        ApiResponse<?> response = new ApiResponse<>(randomTrails);

        return ResponseEntity.ok(randomTrails);
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
        Object response;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                response = this.hikingTrailService.getHikingTrailAuthenticated(trailId, userDetails);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            response =
                    this.hikingTrailService
                            .getApprovedHikingTrailWithApprovedImagesById(trailId, StatusEnum.APPROVED);
            logger.info("No token response");
        }

        return ResponseEntity.ok(response);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOwnedHikingTrail(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.hikingTrailService.deleteOwnedHikingTrailById(trailId, userDetails);

        return ResponseEntity.noContent().build();
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
                        .getAllApprovedHikingTrailsWithLikes(userDetails, pageable, sortByLikedUser);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            allHikingTrails = this.hikingTrailService.getAllApprovedHikingTrails(pageable);
        }

        ApiResponse<Page<?>> response = new ApiResponse<>(allHikingTrails);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<HikingTrailIdDto> createHikingTrail(
            @Valid @RequestBody HikingTrailCreateOrReviewDto createOrReviewDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        logger.debug("Display create hiking trail request {}", hikingTrailCreateOrReviewDto);

        Long newHikingTrailId =
                this.hikingTrailService.createHikingTrail(createOrReviewDto, userDetails);

        HikingTrailIdDto responseDto = new HikingTrailIdDto(newHikingTrailId);

        return ResponseEntity
                .created(URI.create("api/trails/" + newHikingTrailId))
                .body(responseDto);
    }

//    @DeleteMapping("/{id}")
//    ResponseEntity<?> deleteHikingTrail(@PathVariable("id") Long trailId) {
//        boolean success = this.hikingTrailService.deleteHikingTrail(trailId);
//        return ResponseEntity.ok().build();
//    }
    /*TODO: /api/trails/{id}/reviewer moved to superuser controller*/

    @PatchMapping("/{id}/start-point")
    public ResponseEntity<HikingTrailStartPointDto> updateStartPoint(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateStartPointDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailStartPointDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailStartPoint(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/end-point")
    public ResponseEntity<HikingTrailEndPointDto> updateEndPoint(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateEndPointDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailEndPointDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailEndPoint(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/total-distance")
    public ResponseEntity<HikingTrailTotalDistanceDto> updateTotalDistance(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTotalDistanceDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        logger.debug("Display hikingTrailUpdateTotalDistance {}", hikingTrailUpdateTotalDistanceDto);

        HikingTrailTotalDistanceDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailTotalDistance(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/water-availability")
    public ResponseEntity<HikingTrailWaterAvailabilityDto> updateWaterAvailable(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateWaterAvailabilityDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailWaterAvailabilityDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailWaterAvailable(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/activity")
    public ResponseEntity<HikingTrailActivityDto> updateHikingTrailActivity(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateActivityDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailActivityDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailActivity(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/trail-info")
    public ResponseEntity<HikingTrailTrailInfoDto> updateTrailInfo(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTrailInfoDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailTrailInfoDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailTrailInfo(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/elevation-gained")
    public ResponseEntity<HikingTrailElevationGainedDto> updateElevationGained(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateElevationGainedDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailElevationGainedDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailElevationGained(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/trail-difficulty")
    public ResponseEntity<HikingTrailDifficultyDto> updateTrailDifficulty(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTrailDifficultyDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailDifficultyDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailDifficulty(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/available-huts")
    public ResponseEntity<AccommodationWrapperDto> updateAvailableHuts(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateAvailableHutsDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationWrapperDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailAvailableHuts(
                                trailId,
                                updateDto,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED));

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/destinations")
    public ResponseEntity<DestinationWrapperDto> updateDestinations(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateDestinationsDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationWrapperDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailDestinations(
                                trailId,
                                updateDto,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED));

        return ResponseEntity.ok(responseDto);
    }

    /*TODO: Discuss validation message with Ivo*/
    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ApiResponse<Boolean>> changeMainImage(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean updatedMainImage =
                this.hikingTrailUpdateService
                        .updateHikingTrailMainImage(
                                trailId,
                                imageMainUpdateDto,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                        );

        ApiResponse<Boolean> response = new ApiResponse<>(updatedMainImage);

        return ResponseEntity.ok(response);
    }

    /*TODO: returns only messages, no errors*/
    @PatchMapping("/{id}/like")
    public ResponseEntity<LikeResponseDto> toggleTrailLikeStatus(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody LikeRequestDto likeRequestDto
    ) {
        boolean like =
                this.hikingTrailService
                        .likeOrUnlikeTrailAndSave(trailId, likeRequestDto, StatusEnum.APPROVED);

        LikeResponseDto response = new LikeResponseDto(like);

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
                .addNewTrailComment(trailId, commentCreateDto, userDetails, StatusEnum.APPROVED);

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
