package bg.exploreBG.web;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.LikeResponseDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailIdDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.enums.StatusEnum;
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

        return ResponseEntity.ok(allHikingTrails);
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

    @GetMapping("/select")
    public ResponseEntity<List<HikingTrailIdTrailNameDto>> select() {
        List<HikingTrailIdTrailNameDto> selected = this.hikingTrailService.selectAll();

        return ResponseEntity.ok(selected);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getTrailComments(@PathVariable("id") Long trailId) {
        List<CommentDto> comments =
                this.hikingTrailService.getHikingTrailComments(trailId);

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createTrailComment(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody CommentRequestDto createDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto dto =
                this.hikingTrailService
                        .addNewTrailComment(trailId, createDto, userDetails, StatusEnum.APPROVED);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{trailId}/comments/{commentId}")
    public ResponseEntity<Void> deleteTrailComment(
            @PathVariable Long trailId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.hikingTrailService.deleteTrailComment(trailId, commentId, userDetails);

        return ResponseEntity.noContent().build();
    }
}
