package bg.exploreBG.web;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateDto;
import bg.exploreBG.service.AccommodationService;
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
@RequestMapping("/api/accommodations")
public class AccommodationController {

    private final AccommodationService accommodationService;

    public AccommodationController(AccommodationService accommodationService) {
        this.accommodationService = accommodationService;
    }

    @GetMapping("/random")
    public ResponseEntity<List<AccommodationBasicPlusImageDto>> getFourRandomAccommodations() {
        List<AccommodationBasicPlusImageDto> randomAccommodations =
                this.accommodationService.getRandomNumOfAccommodations(4);

        return ResponseEntity.ok(randomAccommodations);
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<AccommodationDetailsDto> getAccommodation(@PathVariable("id") Long id) {
        AccommodationDetailsDto accommodation = this.accommodationService.getAccommodationDetailsById(id);

        return ResponseEntity.ok(accommodation);
    }

    @GetMapping
    public ResponseEntity<Page<AccommodationBasicPlusImageDto>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {

        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<AccommodationBasicPlusImageDto> allHikes =
                this.accommodationService.getAllAccommodations(pageable);

        return ResponseEntity.ok(allHikes);
    }

    @GetMapping("/select")
    public ResponseEntity<List<AccommodationBasicDto>> select() {
        List<AccommodationBasicDto> select = this.accommodationService.selectAll();

        return ResponseEntity.ok(select);
    }

    @PostMapping
    public ResponseEntity<AccommodationIdDto> create(
            @Valid @RequestBody AccommodationCreateDto accommodationCreateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        AccommodationIdDto accommodationIdDto =
                this.accommodationService.createAccommodation(accommodationCreateDto, userDetails);

        return ResponseEntity
                .created(URI.create("/api/accommodations/create/" + accommodationIdDto.id()))
                .body(accommodationIdDto);
    }
}
