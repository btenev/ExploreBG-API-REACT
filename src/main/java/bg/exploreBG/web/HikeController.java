package bg.exploreBG.web;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeDetailsDto;
import bg.exploreBG.service.HikeService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hikes")
public class HikeController {

    public final HikeService hikeService;

    public HikeController(HikeService hikeService) {
        this.hikeService = hikeService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<HikeBasicDto>> getFourRandomHikes() {
        List<HikeBasicDto> randomHikes = this.hikeService.getRandomNumOfHikes(4);

        return ResponseEntity.ok(randomHikes);
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<HikeDetailsDto> getHike(@PathVariable Long id) {
        HikeDetailsDto hike = this.hikeService.getHike(id);

        return ResponseEntity.ok(hike);
    }

    //    @Transactional

    @GetMapping("/all")
    public ResponseEntity<Page<HikeBasicDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {

        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, parameters);

        Page<HikeBasicDto> allHikes = this.hikeService.getAllHikes(pageable);

        return ResponseEntity.ok(allHikes);
    }
}
