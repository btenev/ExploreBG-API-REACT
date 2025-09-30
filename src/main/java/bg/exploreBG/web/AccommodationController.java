package bg.exploreBG.web;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.LikeResponseDto;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateOrReviewDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.service.AccommodationService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/accommodations")
public class AccommodationController {
    private final AccommodationService accommodationService;

    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    /*TODO: NEW: Random with like plus data - api response*/
    @GetMapping("/random")
    public ResponseEntity<?> getFourRandomAccommodations(
            Authentication authentication
    ) {
        List<?> randomAccommodations;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                randomAccommodations = this.accommodationService.getRandomNumOfAccommodationsWithLikes(userDetails, 4);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            randomAccommodations =
                    this.accommodationService.getRandomNumOfAccommodations(4);
        }

        return ResponseEntity.ok(randomAccommodations);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccommodation(
            @PathVariable("id") Long accommodationId,
            Authentication authentication
    ) {
        Object response;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                response =
                        this.accommodationService.getAccommodationAuthenticated(accommodationId, userDetails);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            response = this.accommodationService
                    .getApprovedAccommodationWithApprovedImagesById(accommodationId, StatusEnum.APPROVED);
        }

        return ResponseEntity.ok(response);
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
        Page<?> allAccommodations;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                allAccommodations =
                        this.accommodationService
                                .getAllApprovedAccommodationsWithLikes(userDetails, pageable, sortByLikedUser);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            allAccommodations = this.accommodationService.getAllApprovedAccommodations(pageable);
        }

        return ResponseEntity.ok(allAccommodations);
    }

    @GetMapping("/select")
    public ResponseEntity<List<AccommodationIdAndAccommodationName>> select() {
        List<AccommodationIdAndAccommodationName> select = this.accommodationService.selectAll();

        return ResponseEntity.ok(select);
    }

    @PostMapping
    public ResponseEntity<AccommodationIdDto> create(
            @Valid @RequestBody AccommodationCreateOrReviewDto accommodationCreateOrReviewDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationIdDto accommodationIdDto =
                this.accommodationService.createAccommodation(accommodationCreateOrReviewDto, userDetails);

        return ResponseEntity
                .created(URI.create("/api/accommodations/create/" + accommodationIdDto.id()))
                .body(accommodationIdDto);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getAccommodationComments(@PathVariable("id") Long accommodationId) {
        List<CommentDto> comments =
                this.accommodationService
                        .getAccommodationComments(accommodationId);

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createAccommodationComment(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto comment =
                this.accommodationService
                        .addAccommodationComment(accommodationId, requestDto, userDetails, StatusEnum.APPROVED);

        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{accommodationId}/comments/{commentId}")
    public ResponseEntity<Void> deleteAccommodationComment(
            @PathVariable("accommodationId") Long accommodationId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.accommodationService.deleteAccommodationComment(accommodationId, commentId, userDetails);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<LikeResponseDto> toggleAccommodationLikeStatus(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody LikeRequestDto likeRequestDto
    ) {
        boolean like =
                this.accommodationService
                        .likeOrUnlikeAccommodationAndSave(
                                accommodationId, likeRequestDto, StatusEnum.APPROVED);

        LikeResponseDto response = new LikeResponseDto(like);

        return ResponseEntity.ok(response);
    }
}
