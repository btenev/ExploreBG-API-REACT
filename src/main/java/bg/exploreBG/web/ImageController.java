package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateImageDto;
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

    /*
    TODO: Changed /create/{id} to /user - id is not necessary, @PatchMapping instead of POST - tell Ivo
     */
    @PatchMapping( "/user")
    public ResponseEntity<ApiResponse<ImageIdPlusUrlDto>> saveImage(
//            @PathVariable Long id,
            @Valid @RequestPart("data") ImageCreateImageDto imageCreateImageDto,
            @PermittedImageFileSize @PermittedImageFileFormat @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        ImageIdPlusUrlDto imageIdPlusUrlDto =
                this.imageService
                        .saveProfileImage(
//                                id,
                                imageCreateImageDto,
                                file,
                                userDetails
                        );

        ApiResponse<ImageIdPlusUrlDto> response = new ApiResponse<>(imageIdPlusUrlDto);

        return ResponseEntity
                .created(URI.create("/api/images/" + imageIdPlusUrlDto.id()))
                .body(response);
    }
/*
POST /api/images/user - Creates or updates a single image entity for a user.
DELETE /api/images/user - Deletes a single image entity for a user.

POST /api/images/trail/{id} - Creates a collection of images for the specific trail {id}. ([]multipart file)
DELETE /api/images/trail/{id} - Deletes multiple images for the specific trail {id}. (JSON body: { "ids": [] })

POST /api/images/accommodation/{id} - Creates a collection of images for the specific accommodation {id}. ([]multipart file)
DELETE /api/images/accommodation/{id} - Deletes multiple images for the specific accommodation {id}. (JSON body: { "ids": [] })

POST /api/images/destination/{id} - Creates a collection of images for the specific destination {id}. ([]multipart file)
DELETE /api/images/destination/{id} - Deletes multiple images for the specific destination {id}. (JSON body: { "ids": [] })

POST /api/images/hike/{id} - Creates a collection of images for the specific hike {id}. ([]multipart file)
DELETE /api/images/hike/{id} - Deletes multiple images for the specific hike {id}. (JS
*/

}
