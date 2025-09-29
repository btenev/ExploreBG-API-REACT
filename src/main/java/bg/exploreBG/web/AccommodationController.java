package bg.exploreBG.web;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.LikeResponseDto;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.single.*;
import bg.exploreBG.model.dto.accommodation.validate.*;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.dto.image.single.ImageIdDto;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
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

    @PatchMapping("/{id}/accommodation-name")
    public ResponseEntity<AccommodationNameDto> updateAccommodationName(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateAccommodationNameDto updateAccommodationName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationNameDto accommodationName =
                this.accommodationService.
                        updateAccommodationName(accommodationId, updateAccommodationName, userDetails);

        return ResponseEntity.ok(accommodationName);
    }

    @PatchMapping("/{id}/next-to")
    public ResponseEntity<AccommodationNextToDto> updateNextTo(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateNextToDto nextTo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationNextToDto accommodationNextTo =
                this.accommodationService
                        .updateAccommodationNextTo(accommodationId, nextTo, userDetails);

        return ResponseEntity.ok(accommodationNextTo);
    }

    @PatchMapping("/{id}/phone-number")
    public ResponseEntity<AccommodationPhoneNumberDto> updatePhoneNumber(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdatePhoneNumberDto updatePhoneNumber,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationPhoneNumberDto phoneNumber =
                this.accommodationService.
                        updateAccommodationPhoneNumber(accommodationId, updatePhoneNumber, userDetails);

        return ResponseEntity.ok(phoneNumber);
    }

    @PatchMapping("/{id}/site")
    public ResponseEntity<AccommodationSiteDto> updateSite(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateSiteDto updateSite,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationSiteDto site =
                this.accommodationService.
                        updateAccommodationSite(accommodationId, updateSite, userDetails);

        return ResponseEntity.ok(site);
    }

    @PatchMapping("/{id}/accommodation-info")
    public ResponseEntity<AccommodationInfoDto> updateInfo(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateInfoDto updateInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationInfoDto info =
                this.accommodationService.
                        updateAccommodationInfo(accommodationId, updateInfo, userDetails);

        return ResponseEntity.ok(info);
    }

    @PatchMapping("/{id}/bed-capacity")
    public ResponseEntity<AccommodationBedCapacityDto> updateBedCapacity(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateBedCapacityDto updateBedCapacity,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationBedCapacityDto bedCapacity =
                this.accommodationService
                        .updateAccommodationBedCapacity(accommodationId, updateBedCapacity, userDetails);

        return ResponseEntity.ok(bedCapacity);
    }

    @PatchMapping("/{id}/price-per-bed")
    public ResponseEntity<AccommodationPricePerBedDto> updatePricePerBed(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdatePricePerBed updatePricePerBed,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationPricePerBedDto pricePerBed =
                this.accommodationService
                        .updateAccommodationPricePerBed(accommodationId, updatePricePerBed, userDetails);

        return ResponseEntity.ok(pricePerBed);
    }

    @PatchMapping("/{id}/available-food")
    public ResponseEntity<AccommodationAvailableFoodDto> updateFoodAvailable(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateAvailableFoodDto updateAvailableFood,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationAvailableFoodDto availableFood =
                this.accommodationService
                        .updateAccommodationAvailableFood(accommodationId, updateAvailableFood, userDetails);

        return ResponseEntity.ok(availableFood);
    }

    @PatchMapping("/{id}/access")
    public ResponseEntity<AccommodationAccessibilityDto> updateAccessibility(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateAccessibilityDto accessibility,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationAccessibilityDto access =
                this.accommodationService
                        .updateAccommodationAccessibility(accommodationId, accessibility, userDetails);

        return ResponseEntity.ok(access);
    }

    @PatchMapping("/{id}/type")
    public ResponseEntity<AccommodationTypeDto> updateType(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateTypeDto type,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationTypeDto accommodationType =
                this.accommodationService
                        .updateAccommodationType(accommodationId, type, userDetails);

        return ResponseEntity.ok(accommodationType);
    }

    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ImageIdDto> changeMainImage(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long updatedMainImage =
                this.accommodationService.updateAccommodationMainImage(
                        accommodationId,
                        imageMainUpdateDto,
                        userDetails,
                        List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                );

        return ResponseEntity.ok(new ImageIdDto(updatedMainImage));
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
