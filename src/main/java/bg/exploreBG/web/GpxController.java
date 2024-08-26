package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.GpxCreateNewGpxDto;
import bg.exploreBG.model.dto.GpxUrlDto;
import bg.exploreBG.model.dto.SuccessBooleanDto;
import bg.exploreBG.service.GpxService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/gpx")
public class GpxController {

    private final GpxService gpxService;

    public GpxController(GpxService gpxService) {
        this.gpxService = gpxService;
    }

    @PatchMapping("/trail/{id}")
    public ResponseEntity<ApiResponse<GpxUrlDto>> saveGpxFile(
            @PathVariable Long id,
            @RequestPart("data") GpxCreateNewGpxDto gpxCreateNewGpxDto,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

//        GPX gpx = fileService.parseGpx("https://res.cloudinary.com/dcljilaws/image/files/v1723981216/Gpx/1abc25b7-6442-4ebb-80ad-f23e83ac5be3.gpx");
        GpxUrlDto gpxUrlDto = this.gpxService.saveGpxFileIfOwner(id, gpxCreateNewGpxDto, file, userDetails);

        ApiResponse<GpxUrlDto> response = new ApiResponse<>(gpxUrlDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/trail/{id}")
    public ResponseEntity<ApiResponse<SuccessBooleanDto>> deleteGpxFile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuccessBooleanDto deleted = this.gpxService.deleteGpxFileIfOwner(id, userDetails);

        ApiResponse<SuccessBooleanDto> response = new ApiResponse<>(deleted);

        return ResponseEntity.ok(response);
    }
}
