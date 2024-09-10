package bg.exploreBG.web;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeDetailsDto;
import bg.exploreBG.model.dto.hike.single.HikeIdDto;
import bg.exploreBG.model.dto.hike.validate.HikeCreateDto;
import bg.exploreBG.service.HikeService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
    /*TODO: old: '/all' new: only base */
    @GetMapping
    public ResponseEntity<Page<HikeBasicDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {

        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);
        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<HikeBasicDto> allHikes = this.hikeService.getAllHikes(pageable);

        return ResponseEntity.ok(allHikes);
    }
    /*TODO: old: '/create/{id}' new: only base */
    @Transactional
    @PostMapping
    public ResponseEntity<HikeIdDto> create(
            @Valid @RequestBody HikeCreateDto hikeCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        HikeIdDto hikeId = this.hikeService.createHike(hikeCreateDto, userDetails);

        return ResponseEntity
                .created(URI.create("/api/hikes/create/" + hikeId.id()))
                .body(hikeId);
    }

}
