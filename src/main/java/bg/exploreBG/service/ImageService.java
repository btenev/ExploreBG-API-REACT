package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateImageDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.repository.ImageRepository;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

    public ImageIdPlusUrlDto saveProfileImage(
            ImageCreateImageDto imageCreateImageDto,
            MultipartFile file,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userService.getUserEntityByEmail(userDetails.getUsername());
        ImageEntity userImage = loggedUser.getUserImage();

        if (userImage == null) {
            String cloudinaryId = generateCloudinaryId();

            Map<String, String> cloudinaryResponse =
                    validateUploadResult(file, imageCreateImageDto.folder(), cloudinaryId);

            userImage = createImageEntity(cloudinaryResponse, loggedUser);
        } else {
            String cloudinaryId = userImage.getCloudId();

            Map<String, String> cloudinaryResponse =
                    validateUploadResult(file, imageCreateImageDto.folder(), cloudinaryId);

            String url = cloudinaryResponse.get("url");

            userImage.setImageUrl(url);
        }

        ImageEntity saved = this.imageRepository.save(userImage);

        loggedUser.setUserImage(saved);

        this.userService.saveUserEntity(loggedUser);

        return new ImageIdPlusUrlDto(saved.getId(), saved.getImageUrl());
    }

    public String getUserImageUrlByEmail(UserDetails userDetails) {
        Optional<String> userUrl =
                this.imageRepository.findImageUrlByOwnerEmail(userDetails.getUsername());

        return userUrl.orElse(null);
    }

    public List<ImageIdPlusUrlDto> saveTrailPictures(
            Long trailId,
            ImageCreateImageDto imageCreateImageDto,
            MultipartFile[] files,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailService.getTrailByIdWithStatusAndOwner(trailId, userDetails.getUsername());
        UserEntity loggedUser = currentTrail.getCreatedBy();

        List<ImageEntity> currentTrailImages = currentTrail.getImages();

        int usedSlots = currentTrailImages.size();
        int neededImageSlots = files.length;
        int totalImages = usedSlots + neededImageSlots;

        validateImageSlots(totalImages, currentTrail.getMaxNumberOfImages());

        String folder = imageCreateImageDto.folder();
        List<Map<String, String>> uploadResults = validateUploadResult(files, folder);
        List<ImageEntity> newImageEntities = createMultipleImageEntities(uploadResults, loggedUser);

        List<ImageEntity> savedImages = this.imageRepository.saveAll(newImageEntities);

        if (currentTrailImages.isEmpty()) {
            currentTrail.setMainImage(savedImages.get(0));
        }

        currentTrailImages.addAll(newImageEntities);
        this.hikingTrailService.saveHikingTrailEntity(currentTrail);

        return savedImages.stream()
                .map(e -> new ImageIdPlusUrlDto(e.getId(), e.getImageUrl()))
                .toList();

    }

    private ImageEntity createImageEntity(
            Map<String, String> cloudinaryResponse,
            UserEntity owner
    ) {
        String folder = cloudinaryResponse.get("folder");
        String url = cloudinaryResponse.get("url");
        String cloudinaryId = cloudinaryResponse.get("public_id");

        ImageEntity image = new ImageEntity();
        image.setImageUrl(url);
        image.setFolder(folder);
        image.setCloudId(cloudinaryId);
        image.setOwner(owner);
        return image;
    }

    private List<ImageEntity> createMultipleImageEntities(
            List<Map<String, String>> results,
            UserEntity owner
    ) {
        return results.stream().map(r -> createImageEntity(r, owner)).collect(Collectors.toList());
    }

    private String generateCloudinaryId() {
        return String.valueOf(UUID.randomUUID());
    }

    private void validateImageSlots(int neededSlots, int availableSlots) {
        if (neededSlots > availableSlots) {
            int excessImages = neededSlots - availableSlots;
            throw new AppException("You are trying to upload " + excessImages + " more images than the allowed limit.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, String> validateUploadResult(
            MultipartFile multipartFile,
            String folder,
            String cloudinaryId
    ) {
        Map<String, String> uploadResult =
                this.cloudinaryService.uploadFile(multipartFile, folder, cloudinaryId);

        if (uploadResult == null) {
            throw new AppException("Failed to upload an image. Process aborted.", HttpStatus.BAD_REQUEST);
        }

        return uploadResult;
    }

    private List<Map<String, String>> validateUploadResult(
            MultipartFile[] files,
            String folder
    ) {
        List<Map<String, String>> uploadResults = new ArrayList<>();

        for (MultipartFile file : files) {
            String cloudinaryId = generateCloudinaryId();

            Map<String, String> uploadResult = this.cloudinaryService.uploadFile(file, folder, cloudinaryId);

            if (uploadResult.isEmpty()) {
                throw new AppException("Failed to upload all images. Process aborted.", HttpStatus.BAD_REQUEST);

            }

            uploadResults.add(uploadResult);
        }

        return uploadResults;
    }
}
