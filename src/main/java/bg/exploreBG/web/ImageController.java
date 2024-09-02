package bg.exploreBG.web;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.ApiResponseCollection;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.single.ImageUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateImageDto;
import bg.exploreBG.model.validation.MaxFileSize;
import bg.exploreBG.model.validation.PermittedFileType;
import bg.exploreBG.service.ImageService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

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
    @PatchMapping("/user")
    public ResponseEntity<ApiResponse<ImageIdPlusUrlDto>> saveImage(
//            @PathVariable Long id,
            @Valid @RequestPart("data") ImageCreateImageDto imageCreateImageDto,
            @NotNull(message = "A file must be provided. Please choose a file to upload.")
            @MaxFileSize(maxSize = 3)
            @PermittedFileType(
                    allowedTypes = {"image/png", "image/jpg", "image/jpeg", "image/gif"},
                    message = "Only GIF, PNG, and JPG image formats are allowed."
            )
            @RequestPart("file") MultipartFile file,
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

    @GetMapping("/user")
    public ResponseEntity<ImageUrlDto> getUserImageUrl(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userUrl = this.imageService.getUserImageUrlByEmail(userDetails);

        return ResponseEntity.ok(new ImageUrlDto(userUrl));
    }

    @Transactional
    @PatchMapping("/entity/{id}")
    public ResponseEntity<ApiResponseCollection<ImageIdPlusUrlDto>> saveImages(
            @PathVariable Long id,
            @RequestPart("data") ImageCreateImageDto imageCreateImageDto,
            @RequestPart("file") MultipartFile[] files,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ImageIdPlusUrlDto> urlDto = switch (imageCreateImageDto.folder().toLowerCase()) {
            case "trails" -> imageService.saveTrailPictures(id, imageCreateImageDto, files, userDetails);
     /*       case "accommodation" -> imageService.saveAccommodationPictures(id, imageCreateImageDto, files, userDetails);
            case "destination" -> imageService.saveDestinationPictures(id, imageCreateImageDto, files, userDetails);*/
            default -> throw new AppException("Something went wrong", HttpStatus.BAD_REQUEST);
        };

        ApiResponseCollection<ImageIdPlusUrlDto> response = new ApiResponseCollection<>(urlDto);

        return ResponseEntity.ok(response);
    }
/*
POST /api/images/user - Creates or updates a single image entity for a user.
DELETE /api/images/user - Deletes a single image entity for a user.

PATCH /api/images/trail/{id} - Creates a collection of images for the specific trail {id}. ([]multipart file)
DELETE /api/images/trail/{id} - Deletes multiple images for the specific trail {id}. (JSON body: { "ids": [] })

POST /api/images/accommodation/{id} - Creates a collection of images for the specific accommodation {id}. ([]multipart file)
DELETE /api/images/accommodation/{id} - Deletes multiple images for the specific accommodation {id}. (JSON body: { "ids": [] })

POST /api/images/destination/{id} - Creates a collection of images for the specific destination {id}. ([]multipart file)
DELETE /api/images/destination/{id} - Deletes multiple images for the specific destination {id}. (JSON body: { "ids": [] })

POST /api/images/hike/{id} - Creates a collection of images for the specific hike {id}. ([]multipart file)
DELETE /api/images/hike/{id} - Deletes multiple images for the specific hike {id}. (JS
*/

}
