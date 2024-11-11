package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.gpxFile.GpxUrlDateDto;
import bg.exploreBG.model.validation.PermittedFileType;
import bg.exploreBG.service.GpxService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/gpx")
public class GpxController {
    private static final String GPX = "Gpx";
    private final GpxService gpxService;

    public GpxController(GpxService gpxService) {
        this.gpxService = gpxService;
    }

    @PatchMapping("/trail/{id}")
    public ResponseEntity<ApiResponse<GpxUrlDateDto>> saveGpxFile(
            @PathVariable Long id,
            @RequestParam("file")
            @NotNull(message = "A file must be provided. Please choose a file to upload.")
            @PermittedFileType(message = "Please upload a file with the GPX format.") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        GpxUrlDateDto gpxUrlDto = this.gpxService.saveGpxFileIfOwner(id, GPX, file, userDetails);

        ApiResponse<GpxUrlDateDto> response = new ApiResponse<>(gpxUrlDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/trail/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteGpxFile(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean deleted = this.gpxService.deleteGpxFileIfOwner(id, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(deleted);

        return ResponseEntity.ok(response);
    }
}
