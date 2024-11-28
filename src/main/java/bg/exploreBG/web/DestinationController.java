package bg.exploreBG.web;

import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateDto;
import bg.exploreBG.service.DestinationService;
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
@RequestMapping("/api/destinations")
public class DestinationController {
    private static final Logger logger = LoggerFactory.getLogger(DestinationController.class);
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
        DestinationDetailsDto destination = this.destinationService.getDestinationDetailsById(id);

        return ResponseEntity.ok(destination);
    }

    @GetMapping
    public ResponseEntity<Page<DestinationBasicPlusDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<DestinationBasicPlusDto> allDestinations =
                this.destinationService.getAllDestinations(pageable);

        return ResponseEntity.ok(allDestinations);
    }

    @GetMapping("/select")
    public ResponseEntity<?> select() {
        List<DestinationBasicDto> select = this.destinationService.selectAll();

        return ResponseEntity.ok(select);
    }
    /*TODO: old: '/create/{id}' new: only base */
    @PostMapping
    public ResponseEntity<DestinationIdDto> create(
            @Valid @RequestBody DestinationCreateDto destinationCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.debug("{}", destinationCreateDto);

        DestinationIdDto destinationId =
                this.destinationService.createDestination(destinationCreateDto, userDetails);

        return ResponseEntity
                .created(URI.create("/api/destinations/" + destinationId.id()))
                .body(destinationId);
    }
}
