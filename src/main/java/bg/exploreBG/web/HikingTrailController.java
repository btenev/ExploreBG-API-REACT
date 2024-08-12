package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.single.CommentDeletedReplyDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.service.HikingTrailService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<ApiResponse<List<HikingTrailBasicDto>>> getFourRandomHikingTrails() {
        List<HikingTrailBasicDto> randomTrails =
                this.hikingTrailService.getRandomNumOfHikingTrails(4);

        ApiResponse<List<HikingTrailBasicDto>> response = new ApiResponse<>(randomTrails);

        return ResponseEntity.ok(response);
    }

    /*
    APPROVED
    */
    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HikingTrailDetailsDto>> getHikingTrail(
            @PathVariable Long id
    ) {
        HikingTrailDetailsDto hikingTrail = this.hikingTrailService.getHikingTrail(id);

        ApiResponse<HikingTrailDetailsDto> response = new ApiResponse<>(hikingTrail);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @GetMapping("/{id}/auth")
    public ResponseEntity<ApiResponse<HikingTrailDetailsDto>> getHikingTrailAuth(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailDetailsDto hikingTrail =
                this.hikingTrailService.getHikingTrailAuthenticated(id, userDetails);

        ApiResponse<HikingTrailDetailsDto> response = new ApiResponse<>(hikingTrail);

        return ResponseEntity.ok(response);
    }

    /*
    APPROVED
    */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<HikingTrailBasicDto>>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<HikingTrailBasicDto> allHikingTrails = this.hikingTrailService.getAllHikingTrails(pageable);

        ApiResponse<Page<HikingTrailBasicDto>> response = new ApiResponse<>(allHikingTrails);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<ApiResponse<HikingTrailIdDto>> createHikingTrail(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailCreateOrReviewDto hikingTrailCreateOrReviewDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        logger.debug("Display create hiking trail request {}", hikingTrailCreateOrReviewDto);

        Long newHikingTrailId =
                this.hikingTrailService.createHikingTrail(id, hikingTrailCreateOrReviewDto, userDetails);

        HikingTrailIdDto hikingTrailIdDto = new HikingTrailIdDto(newHikingTrailId);

        ApiResponse<HikingTrailIdDto> response = new ApiResponse<>(hikingTrailIdDto);

        return ResponseEntity
                .created(URI.create("api/trails/" + newHikingTrailId))
                .body(response);
    }

    @PatchMapping("/{id}/update-start-point")
    public ResponseEntity<ApiResponse<HikingTrailStartPointDto>> updateStartPoint(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateStartPointDto hikingTrailUpdateStartPointDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailStartPointDto hikingTrailStartPointDto =
                this.hikingTrailService
                        .updateHikingTrailStartPoint(id, hikingTrailUpdateStartPointDto, userDetails);

        ApiResponse<HikingTrailStartPointDto> response = new ApiResponse<>(hikingTrailStartPointDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-end-point")
    public ResponseEntity<ApiResponse<HikingTrailEndPointDto>> updateEndPoint(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateEndPointDto hikingTrailUpdateEndPointDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailEndPointDto hikingTrailEndPointDto =
                this.hikingTrailService
                        .updateHikingTrailEndPoint(id, hikingTrailUpdateEndPointDto, userDetails);

        ApiResponse<HikingTrailEndPointDto> response = new ApiResponse<>(hikingTrailEndPointDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-total-distance")
    public ResponseEntity<ApiResponse<HikingTrailTotalDistanceDto>> updateTotalDistance(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateTotalDistanceDto hikingTrailUpdateTotalDistanceDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        logger.debug("Display hikingTrailUpdateTotalDistance {}", hikingTrailUpdateTotalDistanceDto);

        HikingTrailTotalDistanceDto hikingTrailTotalDistanceDto =
                this.hikingTrailService
                        .updateHikingTrailTotalDistance(id, hikingTrailUpdateTotalDistanceDto, userDetails);

        ApiResponse<HikingTrailTotalDistanceDto> response = new ApiResponse<>(hikingTrailTotalDistanceDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-water-available")
    public ResponseEntity<ApiResponse<HikingTrailWaterAvailableDto>> updateWaterAvailable(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateWaterAvailableDto hikingTrailUpdateWaterAvailableDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailWaterAvailableDto hikingTrailWaterAvailableDto =
                this.hikingTrailService
                        .updateHikingTrailWaterAvailable(id, hikingTrailUpdateWaterAvailableDto, userDetails);

        ApiResponse<HikingTrailWaterAvailableDto> response = new ApiResponse<>(hikingTrailWaterAvailableDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-activity")
    public ResponseEntity<ApiResponse<HikingTrailActivityDto>> updateHikingTrailActivity(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateActivityDto hikingTrailUpdateActivityDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailActivityDto hikingTrailActivityDto =
                this.hikingTrailService.updateHikingTrailActivity(id, hikingTrailUpdateActivityDto, userDetails);

        ApiResponse<HikingTrailActivityDto> response = new ApiResponse<>(hikingTrailActivityDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-trail-info")
    public ResponseEntity<ApiResponse<HikingTrailTrailInfoDto>> updateTrailInfo(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateTrailInfoDto hikingTrailUpdateTrailInfoDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailTrailInfoDto hikingTrailTrailInfoDto =
                this.hikingTrailService
                        .updateHikingTrailTrailInfo(id, hikingTrailUpdateTrailInfoDto, userDetails);

        ApiResponse<HikingTrailTrailInfoDto> response = new ApiResponse<>(hikingTrailTrailInfoDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-elevation-gained")
    public ResponseEntity<ApiResponse<HikingTrailElevationGainedDto>> updateElevationGained(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateElevationGainedDto hikingTrailUpdateElevationGainedDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailElevationGainedDto hikingTrailElevationGainedDto =
                this.hikingTrailService
                        .updateHikingTrailElevationGained(id, hikingTrailUpdateElevationGainedDto, userDetails);

        ApiResponse<HikingTrailElevationGainedDto> response = new ApiResponse<>(hikingTrailElevationGainedDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-trail-difficulty")
    public ResponseEntity<ApiResponse<HikingTrailDifficultyDto>> updateTrailDifficulty(
            @PathVariable Long id,
            @RequestBody HikingTrailUpdateTrailDifficultyDto hikingTrailUpdateTrailDifficultyDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailDifficultyDto trailDifficultyDto =
                this.hikingTrailService
                        .updateHikingTrailDifficulty(id, hikingTrailUpdateTrailDifficultyDto, userDetails);

        ApiResponse<HikingTrailDifficultyDto> response = new ApiResponse<>(trailDifficultyDto);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PatchMapping("/{id}/update-available-huts")
    public ResponseEntity<ApiResponse<List<AccommodationBasicDto>>> updateAvailableHuts(
            @PathVariable Long id,
            @RequestBody HikingTrailUpdateAvailableHutsDto hikingTrailUpdateAvailableHutsDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<AccommodationBasicDto> accommodationBasicDto =
                this.hikingTrailService
                        .updateHikingTrailAvailableHuts(id, hikingTrailUpdateAvailableHutsDto, userDetails);

        ApiResponse<List<AccommodationBasicDto>> response = new ApiResponse<>(accommodationBasicDto);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PatchMapping("/{id}/update-destinations")
    public ResponseEntity<ApiResponse<List<DestinationBasicDto>>> updateDestinations(
            @PathVariable Long id,
            @RequestBody HikingTrailUpdateDestinationsDto hikingTrailUpdateDestinationsDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<DestinationBasicDto> destinationBasicDto =
                this.hikingTrailService
                        .updateHikingTrailDestinations(id, hikingTrailUpdateDestinationsDto, userDetails);

        ApiResponse<List<DestinationBasicDto>> response = new ApiResponse<>(destinationBasicDto);

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

    @Transactional
    @PostMapping("/create/{id}/comment/{trailId}")
    public ResponseEntity<ApiResponse<CommentDto>> createTrailComment(
            @PathVariable Long id,
            @PathVariable Long trailId,
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto commentDto = this.hikingTrailService
                .addNewTrailComment(id, trailId, commentCreateDto, userDetails);

        ApiResponse<CommentDto> response = new ApiResponse<>(commentDto);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @DeleteMapping("/delete/{commentId}/comment/{trailId}")
    public ResponseEntity<ApiResponse<CommentDeletedReplyDto>> deleteTrailComment(
            @PathVariable Long commentId,
            @PathVariable Long trailId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean removed = this.hikingTrailService.deleteTrailComment(commentId, trailId, userDetails);

        CommentDeletedReplyDto replyDto = new CommentDeletedReplyDto(removed);

        ApiResponse<CommentDeletedReplyDto> response = new ApiResponse<>(replyDto);

        return ResponseEntity.ok(response);
    }
}
