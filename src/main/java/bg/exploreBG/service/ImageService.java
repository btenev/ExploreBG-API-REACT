package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateImageDto;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.repository.ImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserService userService;
    private final CloudinaryService cloudinaryService;

    public ImageService(
            ImageRepository imageRepository,
            UserService userService,
            CloudinaryService cloudinaryService
    ) {
        this.imageRepository = imageRepository;
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
    }

    public ImageIdPlusUrlDto saveProfileImage(
//            Long id,
            ImageCreateImageDto imageCreateImageDto,
            MultipartFile file,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userService.getUserEntityByEmail(userDetails.getUsername());

        ImageEntity userImage = loggedUser.getUserImage();
        ImageEntity newImage;

        if (userImage == null) {
//            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String cloudinaryId = String.valueOf(UUID.randomUUID());

            newImage = createImageEntity(
                    imageCreateImageDto,
                    file,
                    cloudinaryId
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

    private ImageEntity createImageEntity(
            ImageCreateImageDto imageDto,
            MultipartFile file,
            String cloudinaryId
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
}
