package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateNewImageDto;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.repository.ImageRepository;
import bg.exploreBG.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final ImageRepository imageRepository;

    private final UserRepository userRepository;

    public ImageService(
            UserService userService,
            CloudinaryService cloudinaryService,
            ImageRepository imageRepository,
            UserRepository userRepository
    ) {
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
    }

    public ImageIdPlusUrlDto uploadProfileImage(
            Long id,
            ImageCreateNewImageDto imageCreateNewImageDto,
            MultipartFile file,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userService.validUser(id, userDetails);
        String cloudinaryId = validUser.getId().toString();

        ImageEntity newImage = createNewImageEntity(imageCreateNewImageDto, file, cloudinaryId);
        ImageEntity userImage = validUser.getUserImage();
        if (userImage == null) {
            validUser.setUserImage(newImage);
        } else {
            userImage.setImageUrl(newImage.getImageUrl());
            userImage.setImageName(newImage.getImageName());
            validUser.setUserImage(userImage);
        }

        UserEntity savedUser = this.userRepository.save(validUser);
        ImageEntity savedUserImage = savedUser.getUserImage();

        return new ImageIdPlusUrlDto(savedUserImage.getId(), savedUserImage.getImageUrl());
    }

    private ImageEntity createNewImageEntity(
            ImageCreateNewImageDto imageCreateNewImageDto,
            MultipartFile file,
            String cloudinaryId
    ) {
        String cloudinaryUrl = uploadToCloudinary(
                file,
                imageCreateNewImageDto.folder(),
                cloudinaryId
        );

        ImageEntity image = new ImageEntity();
        image.setImageName(imageCreateNewImageDto.name());
        image.setImageUrl(cloudinaryUrl);

        return image;
    }

    private String uploadToCloudinary(
            MultipartFile multipartFile,
            String folder,
            String cloudinaryId
    ) {
        String url = cloudinaryService.uploadFile(multipartFile, folder, cloudinaryId);
        if (url == null) {
            throw new AppException("Invalid image url!", HttpStatus.BAD_REQUEST);
        }
        return url;
    }
}
