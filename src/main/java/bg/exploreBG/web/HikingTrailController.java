package bg.exploreBG.web;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailIdDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailTotalDistance;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailTrailInfo;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateTotalDistance;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateTrailInfo;
import bg.exploreBG.service.HikingTrailService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/trails")
public class HikingTrailController {

    private static final Logger logger = LoggerFactory.getLogger(HikingTrailController.class);

    private final HikingTrailService hikingTrailService;

    public HikingTrailController(HikingTrailService hikingTrailService) {
        this.hikingTrailService = hikingTrailService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<HikingTrailBasicDto>> getFourRandomHikingTrails() {
        List<HikingTrailBasicDto> randomTrails = this.hikingTrailService.getRandomNumOfHikingTrails(4);

        return ResponseEntity.ok(randomTrails);
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<HikingTrailDetailsDto> getHikingTrail(@PathVariable Long id) {
        HikingTrailDetailsDto hikingTrail = this.hikingTrailService.getHikingTrail(id);

        return ResponseEntity.ok(hikingTrail);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<HikingTrailBasicDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, parameters);
        Page<HikingTrailBasicDto> allHikingTrails = this.hikingTrailService.getAllHikingTrails(pageable);

        return ResponseEntity.ok(allHikingTrails);
    }

    @PostMapping("/create/{id}")
    public ResponseEntity<HikingTrailIdDto> createHikingTrail(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailCreateDto hikingTrailCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
//        logger.debug("Display create hiking trail request {}", hikingTrailCreateDto);

        Long newHikingTrailId =
                this.hikingTrailService.createHikingTrail(id, hikingTrailCreateDto, userDetails);

        return ResponseEntity
                .created(URI.create("api/trails/" + newHikingTrailId))
                .body(new HikingTrailIdDto(newHikingTrailId));
    }

    @PatchMapping("/{id}/update-total-distance")
    public ResponseEntity<HikingTrailTotalDistance> updateTotalDistance(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateTotalDistance hikingTrailUpdateTotalDistance,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.debug("Display hikingTrailUpdateTotalDistance {}", hikingTrailUpdateTotalDistance);

        HikingTrailTotalDistance hikingTrailTotalDistance = this.hikingTrailService
                .updateHikingTrailTotalDistance(id, hikingTrailUpdateTotalDistance, userDetails);

        return ResponseEntity.ok(hikingTrailTotalDistance);
    }

    @PatchMapping("/{id}/update-trail-info")
    public ResponseEntity<HikingTrailTrailInfo> updateTrailInfo(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailUpdateTrailInfo hikingTrailUpdateTrailInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikingTrailTrailInfo hikingTrailTrailInfo = this.hikingTrailService
                .updateHikingTrailTrailInfo(id, hikingTrailUpdateTrailInfo, userDetails);

        return ResponseEntity.ok(hikingTrailTrailInfo);
    }
}
