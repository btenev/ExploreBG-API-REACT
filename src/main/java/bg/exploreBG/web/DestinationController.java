package bg.exploreBG.web;

import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.service.DestinationService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<DestinationBasicPlusDto>> getRandomDestination() {
        List<DestinationBasicPlusDto> randomNumOfDestinations =
                this.destinationService.getRandomNumOfDestinations(4);

        return ResponseEntity.ok(randomNumOfDestinations);
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<DestinationDetailsDto> getDestination(@PathVariable Long id) {
        DestinationDetailsDto destination = this.destinationService.getDestination(id);

        return ResponseEntity.ok(destination);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<DestinationBasicPlusDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, parameters);

        Page<DestinationBasicPlusDto> allDestinations =
                this.destinationService.getAllDestinations(pageable);

        return ResponseEntity.ok(allDestinations);
    }

    @GetMapping("/select")
    public ResponseEntity<?> select() {
        List<DestinationBasicDto> select = this.destinationService.selectAll();

        return ResponseEntity.ok(select);
    }
}
