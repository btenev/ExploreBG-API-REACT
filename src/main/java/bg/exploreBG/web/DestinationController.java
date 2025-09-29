package bg.exploreBG.web;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.LikeResponseDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.single.*;
import bg.exploreBG.model.dto.destination.validate.*;
import bg.exploreBG.model.dto.image.single.ImageIdDto;
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
        Object response;

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                response =
                        this.destinationService.getDestinationAuthenticated(destinationId, userDetails);
            } else {
                return ResponseEntity.badRequest().body("Invalid principal type");
            }
        } else {
            response = this.destinationService
                    .getApprovedDestinationWithApprovedImagesById(destinationId, StatusEnum.APPROVED);
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
        logger.warn("{}", like);

        LikeResponseDto response = new LikeResponseDto(like);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getDestinationComments(@PathVariable("id") Long destinationId) {
        List<CommentDto> comments =
                this.destinationService
                        .getDestinationComments(destinationId);

        return ResponseEntity.ok(comments);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createDestinationComment(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto dto =
                this.destinationService
                        .addDestinationComment(destinationId, requestDto, userDetails, StatusEnum.APPROVED);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{destinationId}/comments/{commentId}")
    public ResponseEntity<Void> deleteDestinationComment(
            @PathVariable("destinationId") Long destinationId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        this.destinationService
                .deleteDestinationComment(destinationId, commentId, userDetails);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/destination-name")
    public ResponseEntity<DestinationNameDto> updateDestinationName(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateDestinationNameDto updateDestinationName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationNameDto destinationName =
                this.destinationService
                        .updateDestinationName(destinationId, updateDestinationName, userDetails);

        return ResponseEntity.ok().body(destinationName);
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<DestinationLocationDto> updateDestinationLocation(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateLocationDto updateLocation,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationLocationDto location =
                this.destinationService
                        .updateLocation(destinationId, updateLocation, userDetails);

        return ResponseEntity.ok().body(location);
    }

    @PatchMapping("/{id}/destination-info")
    public ResponseEntity<DestinationInfoDto> updateInfo(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateInfoDto destinationUpdateInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationInfoDto info =
                this.destinationService
                        .updateDestinationInfo(destinationId, destinationUpdateInfo, userDetails);

        return ResponseEntity.ok().body(info);
    }

    @PatchMapping("/{id}/next-to")
    public ResponseEntity<DestinationNextToDto> updateNextTo(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateNextToDto updateNextTo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationNextToDto nextTo =
                this.destinationService
                        .updateNextTo(destinationId, updateNextTo, userDetails);

        return ResponseEntity.ok().body(nextTo);
    }

    @PatchMapping("/{id}/type")
    public ResponseEntity<DestinationTypeDto> updateType(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateTypeDto updateType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationTypeDto type =
                this.destinationService
                        .updateType(destinationId, updateType, userDetails);

        return ResponseEntity.ok().body(type);
    }

    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ImageIdDto> changeMainImage(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long updateMainImage =
                this.destinationService
                        .updateDestinationMainImage(
                                destinationId,
                                imageMainUpdate,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                        );

        return ResponseEntity.ok(new ImageIdDto(updateMainImage));
    }

}
