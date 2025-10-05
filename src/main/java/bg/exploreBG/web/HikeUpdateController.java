package bg.exploreBG.web;

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
    private final HikeUpdateService  hikeUpdateService;

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
}
