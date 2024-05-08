package bg.exploreBG.web;

import bg.exploreBG.model.dto.HikeDetailsDto;
import bg.exploreBG.model.dto.HikingTrailBasicDto;
import bg.exploreBG.model.dto.HikingTrailDetailsDto;
import bg.exploreBG.service.HikingTrailService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/trails")
public class HikingTrailController {

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
}
