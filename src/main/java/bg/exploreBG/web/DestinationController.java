package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.service.DestinationService;
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
@RequestMapping("/api/destinations")
public class DestinationController {
    private static final Logger logger = LoggerFactory.getLogger(DestinationController.class);
    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping("/random")
    public ResponseEntity<?> getRandomDestination(
            Authentication authentication
    ) {
        List<?> randomDestinations;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                randomDestinations = this.destinationService.getRandomNumOfDestinationsWithLikes(userDetails, 4);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            randomDestinations = this.destinationService.getRandomNumOfDestinations(4);
        }

        ApiResponse<?> response = new ApiResponse<>(randomDestinations);

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<?> getDestination(
            @PathVariable("id") Long destinationId,
            Authentication authentication
    ) {
        ApiResponse<?> response;
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                response = new ApiResponse<>(
                        this.destinationService.getDestinationAuthenticated(destinationId, userDetails));
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            response = new ApiResponse<>(this.destinationService.getDestinationDetailsById(destinationId));
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
        Page<?> allDestinations;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                allDestinations =
                        this.destinationService
                                .getAllApprovedDestinationsWithLikes(userDetails, pageable, sortByLikedUser);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            allDestinations = this.destinationService.getAllApprovedDestinations(pageable);
        }

        return ResponseEntity.ok(allDestinations);
    }

    @GetMapping("/select")
    public ResponseEntity<?> select() {
        List<DestinationIdAndDestinationNameDto> select = this.destinationService.selectAll();

        return ResponseEntity.ok(select);
    }

    /*TODO: old: '/create/{id}' new: only base */
    @PostMapping
    public ResponseEntity<DestinationIdDto> create(
            @Valid @RequestBody DestinationCreateDto destinationCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.debug("{}", destinationCreateDto);

        DestinationIdDto destinationId =
                this.destinationService.createDestination(destinationCreateDto, userDetails);

        return ResponseEntity
                .created(URI.create("/api/destinations/" + destinationId.id()))
                .body(destinationId);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Boolean>> toggleDestinationLike(
            @PathVariable("id") Long destinationId,
            @RequestBody LikeBooleanDto likeBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success =
                this.destinationService
                        .likeOrUnlikeDestinationAndSave(destinationId, likeBooleanDto, userDetails, StatusEnum.APPROVED);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }
}
