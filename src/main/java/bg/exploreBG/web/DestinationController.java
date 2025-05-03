package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.LikeResponseDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.single.CommentDeletedReplyDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.single.*;
import bg.exploreBG.model.dto.destination.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
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

        return ResponseEntity.ok(randomDestinations);
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

//    @DeleteMapping("/{id}")
//    public ResponseEntity<ApiResponse<Boolean>> deleteDestination(
//            @PathVariable("id") Long destinationId,
//            @AuthenticationPrincipal UserDetails userDetails
//    ) {
//        boolean success = this.destinationService.deleteOwnedDestinationById(destinationId, userDetails);
//
//        ApiResponse<Boolean> response = new ApiResponse<>(success);
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/select")
    public ResponseEntity<?> select() {
        List<DestinationIdAndDestinationNameDto> select = this.destinationService.selectAll();

        return ResponseEntity.ok(select);
    }

    /*TODO: old: '/create/{id}' new: only base */
    @PostMapping
    public ResponseEntity<DestinationIdDto> create(
            @Valid @RequestBody DestinationCreateOrReviewDto destinationCreateOrReviewDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.debug("{}", destinationCreateOrReviewDto);

        DestinationIdDto destinationId =
                this.destinationService.createDestination(destinationCreateOrReviewDto, userDetails);

        return ResponseEntity
                .created(URI.create("/api/destinations/" + destinationId.id()))
                .body(destinationId);
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<LikeResponseDto> toggleDestinationLikeStatus(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody LikeRequestDto likeRequestDto
    ) {
        boolean like =
                this.destinationService
                        .likeOrUnlikeDestinationAndSave(destinationId, likeRequestDto, StatusEnum.APPROVED);

        LikeResponseDto response = new LikeResponseDto(like);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentDto>> createDestinationComment(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto comment =
                this.destinationService
                        .addDestinationComment(destinationId, commentCreateDto, userDetails, StatusEnum.APPROVED);

        ApiResponse<CommentDto> response = new ApiResponse<>(comment);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{destinationId}/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentDeletedReplyDto>> deleteDestinationComment(
            @PathVariable("destinationId") Long destinationId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean removed =
                this.destinationService
                        .deleteDestinationComment(destinationId, commentId, userDetails);

        CommentDeletedReplyDto replyDto = new CommentDeletedReplyDto(removed);

        ApiResponse<CommentDeletedReplyDto> response = new ApiResponse<>(replyDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/destination-name")
    public ResponseEntity<ApiResponse<DestinationNameDto>> updateDestinationName(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateDestinationNameDto updateDestinationName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationNameDto destinationName =
                this.destinationService
                        .updateDestinationName(destinationId, updateDestinationName, userDetails);

        ApiResponse<DestinationNameDto> response = new ApiResponse<>(destinationName);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<ApiResponse<DestinationLocationDto>> updateDestinationLocation(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateLocationDto updateLocation,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationLocationDto location =
                this.destinationService
                        .updateLocation(destinationId, updateLocation, userDetails);

        ApiResponse<DestinationLocationDto> response = new ApiResponse<>(location);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/destination-info")
    public ResponseEntity<ApiResponse<DestinationInfoDto>> updateInfo(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateInfoDto destinationUpdateInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationInfoDto info =
                this.destinationService
                        .updateDestinationInfo(destinationId, destinationUpdateInfo, userDetails);

        ApiResponse<DestinationInfoDto> response = new ApiResponse<>(info);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/next-to")
    public ResponseEntity<ApiResponse<DestinationNextToDto>> updateNextTo(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateNextToDto updateNextTo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationNextToDto nextTo =
                this.destinationService
                        .updateNextTo(destinationId, updateNextTo, userDetails);

        ApiResponse<DestinationNextToDto> response = new ApiResponse<>(nextTo);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/type")
    public ResponseEntity<ApiResponse<DestinationTypeDto>> updateType(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateTypeDto updateType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationTypeDto type =
                this.destinationService
                        .updateType(destinationId, updateType, userDetails);

        ApiResponse<DestinationTypeDto> response = new ApiResponse<>(type);

        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ApiResponse<Boolean>> changeMainImage(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean updateMainImage =
                this.destinationService
                        .updateDestinationMainImage(
                                destinationId,
                                imageMainUpdate,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                        );

        ApiResponse<Boolean> response = new ApiResponse<>(updateMainImage);

        return ResponseEntity.ok(response);
    }

}
