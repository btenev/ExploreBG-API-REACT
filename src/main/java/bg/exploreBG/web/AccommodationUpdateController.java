package bg.exploreBG.web;

import bg.exploreBG.model.dto.accommodation.single.*;
import bg.exploreBG.model.dto.accommodation.validate.*;
import bg.exploreBG.model.dto.image.single.ImageIdDto;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.service.AccommodationUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
public class AccommodationUpdateController {
    private final AccommodationUpdateService accommodationUpdateService;

    public AccommodationUpdateController(AccommodationUpdateService accommodationUpdateService) {
        this.accommodationUpdateService = accommodationUpdateService;
    }

    @PatchMapping("/{id}/accommodation-name")
    public ResponseEntity<AccommodationNameDto> updateAccommodationName(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationUpdateAccommodationNameDto updateAccommodationName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationNameDto accommodationName =
                this.accommodationUpdateService.
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
                this.accommodationUpdateService
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
                this.accommodationUpdateService.
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
                this.accommodationUpdateService.
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
                this.accommodationUpdateService.
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
                this.accommodationUpdateService
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
                this.accommodationUpdateService
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
                this.accommodationUpdateService
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
                this.accommodationUpdateService
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
                this.accommodationUpdateService
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
                this.accommodationUpdateService.updateAccommodationMainImage(
                        accommodationId,
                        imageMainUpdateDto,
                        userDetails,
                        List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                );

        return ResponseEntity.ok(new ImageIdDto(updatedMainImage));
    }
}
