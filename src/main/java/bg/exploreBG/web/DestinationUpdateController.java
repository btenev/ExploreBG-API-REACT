package bg.exploreBG.web;

import bg.exploreBG.model.dto.destination.single.*;
import bg.exploreBG.model.dto.destination.validate.*;
import bg.exploreBG.model.dto.image.single.ImageIdDto;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.service.DestinationUpdateService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
public class DestinationUpdateController {
    private final DestinationUpdateService destinationUpdateService;

    public DestinationUpdateController(DestinationUpdateService destinationUpdateService) {
        this.destinationUpdateService = destinationUpdateService;
    }

    @PatchMapping("/{id}/destination-name")
    public ResponseEntity<DestinationNameDto> updateDestinationName(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateDestinationNameDto updateDestinationName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationNameDto destinationName =
                this.destinationUpdateService
                        .updateDestinationName(destinationId, updateDestinationName, userDetails);

        return ResponseEntity.ok().body(destinationName);
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<DestinationLocationDto> updateDestinationLocation(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateLocationDto updateLocation,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationLocationDto location =
                this.destinationUpdateService
                        .updateLocation(destinationId, updateLocation, userDetails);

        return ResponseEntity.ok().body(location);
    }

    @PatchMapping("/{id}/destination-info")
    public ResponseEntity<DestinationInfoDto> updateInfo(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateInfoDto destinationUpdateInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationInfoDto info =
                this.destinationUpdateService
                        .updateDestinationInfo(destinationId, destinationUpdateInfo, userDetails);

        return ResponseEntity.ok().body(info);
    }

    @PatchMapping("/{id}/next-to")
    public ResponseEntity<DestinationNextToDto> updateNextTo(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateNextToDto updateNextTo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationNextToDto nextTo =
                this.destinationUpdateService
                        .updateNextTo(destinationId, updateNextTo, userDetails);

        return ResponseEntity.ok().body(nextTo);
    }

    @PatchMapping("/{id}/type")
    public ResponseEntity<DestinationTypeDto> updateType(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationUpdateTypeDto updateType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        DestinationTypeDto type =
                this.destinationUpdateService
                        .updateType(destinationId, updateType, userDetails);

        return ResponseEntity.ok().body(type);
    }

    @PatchMapping("/{id}/main-image")
    public ResponseEntity<ImageIdDto> changeMainImage(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody ImageMainUpdateDto imageMainUpdate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long updateMainImage =
                this.destinationUpdateService
                        .updateDestinationMainImage(
                                destinationId,
                                imageMainUpdate,
                                userDetails,
                                List.of(StatusEnum.PENDING, StatusEnum.APPROVED)
                        );

        return ResponseEntity.ok(new ImageIdDto(updateMainImage));
    }
}
