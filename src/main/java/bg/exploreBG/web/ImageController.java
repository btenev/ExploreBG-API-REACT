package bg.exploreBG.web;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.ApiResponseCollection;
import bg.exploreBG.model.dto.EntityIdsToDeleteDto;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainDto;
import bg.exploreBG.model.dto.image.single.ImageUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.validation.MaxFileSize;
import bg.exploreBG.model.validation.PermittedFileType;
import bg.exploreBG.service.ImageService;
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

    @PatchMapping("/user")
    public ResponseEntity<ApiResponse<ImageIdPlusUrlDto>> saveImage(
//            @PathVariable Long id,
            @Valid @RequestPart("data") ImageCreateDto imageCreateDto,
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
                                imageCreateDto,
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

    @PatchMapping("/entity/{id}")
    public ResponseEntity<ApiResponseCollection<ImageIdUrlIsMainDto>> saveImages(
            @PathVariable Long id,
            @RequestPart("data") ImageCreateDto imageCreateDto,
            @RequestPart("file") MultipartFile[] files,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<ImageIdUrlIsMainDto> urlDto = switch (imageCreateDto.folder().toLowerCase()) {
            case "trails-demo", "trails" -> this.imageService.saveTrailPictures(
                    id, imageCreateDto, files, userDetails, List.of(StatusEnum.PENDING, StatusEnum.APPROVED));
            /*       case "accommodation" -> imageService.saveAccommodationPictures(id, imageCreateDto, files, userDetails);
            case "destination" -> imageService.saveDestinationPictures(id, imageCreateDto, files, userDetails);*/
            default -> throw new AppException("Something went wrong", HttpStatus.BAD_REQUEST);
        };

        ApiResponseCollection<ImageIdUrlIsMainDto> response = new ApiResponseCollection<>(urlDto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/entity/{id}")
    public ResponseEntity<ApiResponse<Boolean>> deleteImages(
            @PathVariable Long id,
            @RequestBody EntityIdsToDeleteDto toDeleteDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = switch (toDeleteDto.folder().toLowerCase()) {
            case "trails" -> this.imageService.deleteTrailPicturesById(id, toDeleteDto, userDetails);
            default -> throw new IllegalStateException("Unexpected value: " + toDeleteDto.folder().toLowerCase());
        };

        ApiResponse<Boolean> response = new ApiResponse<>(success);

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
