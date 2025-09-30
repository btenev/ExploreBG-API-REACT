package bg.exploreBG.web;

import bg.exploreBG.model.dto.accommodation.AccommodationWrapperDto;
import bg.exploreBG.model.dto.destination.DestinationWrapperDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.image.single.ImageIdDto;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.service.HikingTrailUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trails")
public class HikingTrailUpdateController {
    private final HikingTrailUpdateService hikingTrailUpdateService;

    public HikingTrailUpdateController(HikingTrailUpdateService hikingTrailUpdateService) {
        this.hikingTrailUpdateService = hikingTrailUpdateService;
    }

    @PatchMapping("/{id}/start-point")
    public ResponseEntity<HikingTrailStartPointDto> updateStartPoint(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateStartPointDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailStartPointDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailStartPoint(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/end-point")
    public ResponseEntity<HikingTrailEndPointDto> updateEndPoint(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateEndPointDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailEndPointDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailEndPoint(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/total-distance")
    public ResponseEntity<HikingTrailTotalDistanceDto> updateTotalDistance(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTotalDistanceDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailTotalDistanceDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailTotalDistance(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/water-availability")
    public ResponseEntity<HikingTrailWaterAvailabilityDto> updateWaterAvailable(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateWaterAvailabilityDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailWaterAvailabilityDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailWaterAvailable(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/activity")
    public ResponseEntity<HikingTrailActivityDto> updateHikingTrailActivity(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateActivityDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailActivityDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailActivity(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/trail-info")
    public ResponseEntity<HikingTrailTrailInfoDto> updateTrailInfo(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTrailInfoDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailTrailInfoDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailTrailInfo(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/elevation-gained")
    public ResponseEntity<HikingTrailElevationGainedDto> updateElevationGained(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateElevationGainedDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailElevationGainedDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailElevationGained(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/trail-difficulty")
    public ResponseEntity<HikingTrailDifficultyDto> updateTrailDifficulty(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailUpdateTrailDifficultyDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailDifficultyDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailDifficulty(trailId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/available-huts")
    public ResponseEntity<AccommodationWrapperDto> updateAvailableHuts(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateAvailableHutsDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationWrapperDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailAvailableHuts(
                                trailId,
                                updateDto,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED));

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/destinations")
    public ResponseEntity<DestinationWrapperDto> updateDestinations(
            @PathVariable("id") Long trailId,
            @RequestBody HikingTrailUpdateDestinationsDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationWrapperDto responseDto =
                this.hikingTrailUpdateService
                        .updateHikingTrailDestinations(
                                trailId,
                                updateDto,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED));

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ImageIdDto> changeMainImage(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long updatedMainImage =
                this.hikingTrailUpdateService
                        .updateHikingTrailMainImage(
                                trailId,
                                imageMainUpdateDto,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                        );

        return ResponseEntity.ok(new ImageIdDto(updatedMainImage));
    }
}
