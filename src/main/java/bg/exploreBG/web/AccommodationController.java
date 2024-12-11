package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.single.*;
import bg.exploreBG.model.dto.accommodation.validate.*;
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

    @PatchMapping("/{id}/accommodation-name")
    public ResponseEntity<ApiResponse<AccommodationNameDto>> updateAccommodationName(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateAccommodationNameDto updateAccommodationName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationNameDto accommodationName =
                this.accommodationService.
                        updateAccommodationName(accommodationId, updateAccommodationName, userDetails);

        ApiResponse<AccommodationNameDto> response = new ApiResponse<>(accommodationName);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/phone-number")
    public ResponseEntity<ApiResponse<AccommodationPhoneNumberDto>> updatePhoneNumber(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdatePhoneNumberDto updatePhoneNumber,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationPhoneNumberDto phoneNumber =
                this.accommodationService.
                        updateAccommodationPhoneNumber(accommodationId, updatePhoneNumber, userDetails);

        ApiResponse<AccommodationPhoneNumberDto> response = new ApiResponse<>(phoneNumber);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/site")
    public ResponseEntity<ApiResponse<AccommodationSiteDto>> updateSite(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateSiteDto updateSite,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationSiteDto site =
                this.accommodationService.
                        updateAccommodationSite(accommodationId, updateSite, userDetails);

        ApiResponse<AccommodationSiteDto> response = new ApiResponse<>(site);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/accommodation-info")
    public ResponseEntity<ApiResponse<AccommodationInfoDto>> updateInfo(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateInfoDto updateInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationInfoDto info =
                this.accommodationService.
                        updateAccommodationInfo(accommodationId, updateInfo, userDetails);

        ApiResponse<AccommodationInfoDto> response = new ApiResponse<>(info);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/bed-capacity")
    public ResponseEntity<ApiResponse<AccommodationBedCapacityDto>> updateBedCapacity(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateBedCapacityDto updateBedCapacity,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationBedCapacityDto bedCapacity =
                this.accommodationService
                        .updateAccommodationBedCapacity(accommodationId, updateBedCapacity, userDetails);

        ApiResponse<AccommodationBedCapacityDto> response = new ApiResponse<>(bedCapacity);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/price-per-bed")
    public ResponseEntity<ApiResponse<AccommodationPricePerBedDto>> updatePricePerBed(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdatePricePerBed updatePricePerBed,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationPricePerBedDto pricePerBed =
                this.accommodationService
                        .updateAccommodationPricePerBed(accommodationId, updatePricePerBed, userDetails);

        ApiResponse<AccommodationPricePerBedDto> response = new ApiResponse<>(pricePerBed);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/food-available")
    public ResponseEntity<ApiResponse<AccommodationAvailableFoodDto>> updateFoodAvailable(
            @PathVariable("id") Long accommodationId,
            @RequestBody AccommodationUpdateAvailableFoodDto updateAvailableFood,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationAvailableFoodDto availableFood =
                this.accommodationService
                        .updateAccommodationAvailableFood(accommodationId, updateAvailableFood, userDetails);

        ApiResponse<AccommodationAvailableFoodDto> response = new ApiResponse<>(availableFood);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/access")
    public ResponseEntity<ApiResponse<AccommodationAccessibilityDto>> updateAccessibility(
            @PathVariable("id") Long accommodationId,
            @RequestBody AccommodationUpdateAccessibilityDto accessibility,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationAccessibilityDto access =
                this.accommodationService
                        .updateAccommodationAccessibility(accommodationId, accessibility, userDetails);

        ApiResponse<AccommodationAccessibilityDto> response = new ApiResponse<>(access);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/type")
    public ResponseEntity<ApiResponse<AccommodationTypeDto>> updateType(
            @PathVariable("id") Long accommodationId,
            @RequestBody AccommodationUpdateTypeDto type,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationTypeDto accommodationType =
                this.accommodationService
                        .updateAccommodationType(accommodationId, type, userDetails);

        ApiResponse<AccommodationTypeDto> response = new ApiResponse<>(accommodationType);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/next-to")
    public ResponseEntity<ApiResponse<AccommodationNextToDto>> updateNextTo(
            @PathVariable("id") Long accommodationId,
            @RequestBody AccommodationUpdateNextToDto nextTo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationNextToDto accommodationNextTo =
                this.accommodationService
                        .updateAccommodationNextTo(accommodationId, nextTo, userDetails);

        ApiResponse<AccommodationNextToDto> response = new ApiResponse<>(accommodationNextTo);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ApiResponse<Boolean>> changeMainImage(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean updatedMainImage =
                this.accommodationService.updateAccommodationMainImage(
                        accommodationId,
                        imageMainUpdateDto,
                        userDetails,
                        List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                );

        ApiResponse<Boolean> response = new ApiResponse<>(updatedMainImage);

        return ResponseEntity.ok(response);
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
