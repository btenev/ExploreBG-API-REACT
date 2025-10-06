package bg.exploreBG.web;

import bg.exploreBG.model.dto.EntityInfoDto;
import bg.exploreBG.model.dto.NextToDto;
import bg.exploreBG.model.dto.accommodation.single.AccommodationNextToDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationUpdateNextToDto;
import bg.exploreBG.model.dto.hike.single.HikeDateDto;
import bg.exploreBG.model.dto.hike.validate.HikeUpdateDateDto;
import bg.exploreBG.model.dto.hike.validate.HikeUpdateInfoDto;
import bg.exploreBG.model.dto.hike.validate.HikeUpdateNextToDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailEndPointDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailStartPointDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateEndPointDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateStartPointDto;
import bg.exploreBG.service.HikeUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hikes")
public class HikeUpdateController {
    private final HikeUpdateService hikeUpdateService;

    public HikeUpdateController(HikeUpdateService hikeUpdateService) {
        this.hikeUpdateService = hikeUpdateService;
    }

    @PatchMapping("/{id}/start-point")
    public ResponseEntity<HikingTrailStartPointDto> updateStartPoint(
            @PathVariable("id") Long hikeId,
            @Valid @RequestBody HikingTrailUpdateStartPointDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailStartPointDto responseDto =
                this.hikeUpdateService
                        .updateHikeStartPoint(hikeId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/end-point")
    public ResponseEntity<HikingTrailEndPointDto> updateEndPoint(
            @PathVariable("id") Long hikeId,
            @Valid @RequestBody HikingTrailUpdateEndPointDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailEndPointDto responseDto =
                this.hikeUpdateService
                        .updateHikeEndPoint(hikeId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/hike-date")
    public ResponseEntity<HikeDateDto> updateHikeDate(
            @PathVariable("id") Long hikeId,
            @Valid @RequestBody HikeUpdateDateDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikeDateDto responseDto = this.hikeUpdateService.updateHikeDate(hikeId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/next-to")
    public ResponseEntity<NextToDto> updateNextTo(
            @PathVariable("id") Long hikeId,
            @Valid @RequestBody HikeUpdateNextToDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        NextToDto accommodationNextTo = this.hikeUpdateService.updateHikeNextTo(hikeId, updateDto, userDetails);

        return ResponseEntity.ok(accommodationNextTo);
    }

    @PatchMapping("/{id}/hike-info")
    public ResponseEntity<EntityInfoDto> updateHikeInfo(
            @PathVariable("id") Long hikeId,
            @Valid @RequestBody HikeUpdateInfoDto updateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        EntityInfoDto responseDto = this.hikeUpdateService.updateHikeInfo(hikeId, updateDto, userDetails);

        return ResponseEntity.ok(responseDto);
    }
}
