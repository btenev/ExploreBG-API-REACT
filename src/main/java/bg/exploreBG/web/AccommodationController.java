package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateDto;
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

        ApiResponse<?> response = new ApiResponse<>(randomAccommodations);

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccommodation(
            @PathVariable("id") Long accommodationId,
            Authentication authentication
    ) {
        ApiResponse<?> response;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                response = new ApiResponse<>(
                        this.accommodationService.getAccommodationAuthenticated(accommodationId, userDetails));
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            response = new ApiResponse<>(this.accommodationService.getAccommodationDetailsById(accommodationId));
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
            @Valid @RequestBody AccommodationCreateDto accommodationCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationIdDto accommodationIdDto =
                this.accommodationService.createAccommodation(accommodationCreateDto, userDetails);

        return ResponseEntity
                .created(URI.create("/api/accommodations/create/" + accommodationIdDto.id()))
                .body(accommodationIdDto);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleAccommodationLike(
            @PathVariable("id") Long accommodationId,
            @RequestBody LikeBooleanDto likeBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success =
                this.accommodationService
                        .likeOrUnlikeAccommodationAndSave(
                                accommodationId, likeBooleanDto, userDetails, StatusEnum.APPROVED);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }
}
