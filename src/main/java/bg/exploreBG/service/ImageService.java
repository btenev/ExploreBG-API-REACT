package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateImageDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.repository.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserService userService;
    private final HikingTrailService hikingTrailService;
    private final AccommodationService accommodationService;
    private final CloudinaryService cloudinaryService;

    public ImageService(
            ImageRepository imageRepository,
            UserService userService,
            HikingTrailService hikingTrailService,
            AccommodationService accommodationService,
            CloudinaryService cloudinaryService
    ) {
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.hikingTrailService = hikingTrailService;
        this.accommodationService = accommodationService;
        this.cloudinaryService = cloudinaryService;
    }

    public ImageIdPlusUrlDto saveProfilePicture(
            ImageCreateImageDto imageCreateImageDto,
            MultipartFile file,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userService.getUserEntityByEmail(userDetails.getUsername());

        ImageEntity userImage = loggedUser.getUserImage();
        ImageEntity newImage;

        if (userImage == null) {
//            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String cloudinaryId = generateCloudinaryId();

            newImage = createImageEntity(
                    imageCreateImageDto,
                    file,
                    cloudinaryId,
                    loggedUser
            );

        } else {
            newImage = updateImageEntity(
                    imageCreateImageDto,
                    file,
                    userImage
            );
        }

        ImageEntity saved = this.imageRepository.save(newImage);

        loggedUser.setUserImage(saved);

        this.userService.saveUserEntity(loggedUser);

        return new ImageIdPlusUrlDto(saved.getId(), saved.getImageUrl());
    }

/*    public void saveTrailPictures(
            Long trailId,
            ImageCreateImageDto imageCreateImageDto,
            MultipartFile[] files,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailService.getTrailByIdWithStatusAndOwner(trailId, userDetails.getUsername());

        List<ImageEntity> images = currentTrail.getImages();
        int trailImageNumber = images.size();
        int availableImageSlots = currentTrail.getMaxNumberOfPictures();
        int totalImages = trailImageNumber + files.length;

        if (totalImages > availableImageSlots) {
            int excessImages = totalImages - availableImageSlots;
            throw new AppException("You are trying to upload " + excessImages + " more images than allowed.",
                    HttpStatus.BAD_REQUEST);
        }


    }*/

    private ImageEntity createImageEntity(
            ImageCreateImageDto imageDto,
            MultipartFile file,
            String cloudinaryId,
            UserEntity loggedUser
    ) {
        Map<String, String> response = validateUploadResult(
                file,
                imageDto.folder(),
                cloudinaryId
        );

        ImageEntity image = new ImageEntity();
        image.setImageName(imageDto.name());
        image.setImageUrl(response.get("url"));
        image.setFolder(response.get("folder"));
        image.setCloudId(response.get("public_id"));
        image.setOwner(loggedUser);
        return image;
    }

    private ImageEntity updateImageEntity(
            ImageCreateImageDto imageCreateImageDto,
            MultipartFile file,
            ImageEntity current
    ) {
        Map<String, String> response = validateUploadResult(
                file,
                imageCreateImageDto.folder(),
                current.getCloudId()
        );

        current.setImageName(imageCreateImageDto.name());
        current.setImageUrl(response.get("url"));

        return current;
    }

    private String generateCloudinaryId() {
        return String.valueOf(UUID.randomUUID());
    }

    private Map<String, String> validateUploadResult(
            MultipartFile multipartFile,
            String folder,
            String cloudinaryId
    ) {
        Map<String, String> cloudinaryResult = cloudinaryService.uploadFile(multipartFile, folder, cloudinaryId);
        if (cloudinaryResult.isEmpty()) {
            throw new AppException("Invalid image url!", HttpStatus.BAD_REQUEST);
        }
        return cloudinaryResult;
    }

    public String getUserImageUrlByEmail(UserDetails userDetails) {
        Optional<String> userUrl =
                this.imageRepository.findImageUrlByOwnerEmail(userDetails.getUsername());

        return userUrl.orElse(null);
    }
}
