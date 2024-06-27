package bg.exploreBG.web;

import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateNewImageDto;
import bg.exploreBG.model.validation.PermittedImageFileFormat;
import bg.exploreBG.model.validation.PermittedImageFileSize;
import bg.exploreBG.service.ImageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(value = "/upload/{id}")
    public ResponseEntity<ImageIdPlusUrlDto> upload(
            @PathVariable Long id,
            @Valid @RequestPart("data") ImageCreateNewImageDto imageCreateNewImageDto,
            @PermittedImageFileSize @PermittedImageFileFormat @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

//        if (imageCreateNewImageDto.folder().equals("User")) {}
        ImageIdPlusUrlDto imageIdPlusUrlDto =
                this.imageService
                        .uploadProfileImage(
                                id,
                                imageCreateNewImageDto,
                                file,
                                userDetails
                        );

        return ResponseEntity
                .created(URI.create("/api/images/" + imageIdPlusUrlDto.id()))
                .body(imageIdPlusUrlDto);
    }
}
