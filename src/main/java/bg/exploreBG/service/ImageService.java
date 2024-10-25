package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.EntityIdsToDeleteDto;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.ImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ImageService {

    private final Logger logger = LoggerFactory.getLogger(ImageService.class);
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
            ImageCreateDto imageCreateDto,
            MultipartFile file,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userService.getUserEntityByEmail(userDetails.getUsername());
        ImageEntity userImage = loggedUser.getUserImage();

        if (userImage == null) {
            String cloudinaryId = generateCloudinaryId();

            Map<String, String> cloudinaryResponse =
                    validateUploadResult(file, imageCreateDto.folder(), cloudinaryId);

            userImage = createImageEntity(cloudinaryResponse, loggedUser, true);
        } else {
            String cloudinaryId = userImage.getCloudId();

            Map<String, String> cloudinaryResponse =
                    validateUploadResult(file, imageCreateDto.folder(), cloudinaryId);

            String url = cloudinaryResponse.get("url");

            userImage.setImageUrl(url);
        }

        ImageEntity saved = this.imageRepository.save(userImage);

        loggedUser.setUserImage(saved);

        this.userService.saveUserWithoutReturn(loggedUser);

        return new ImageIdPlusUrlDto(saved.getId(), saved.getImageUrl());
    }

    public String getUserImageUrlByEmail(UserDetails userDetails) {
        Optional<String> userUrl =
                this.imageRepository.findImageUrlByOwnerEmail(userDetails.getUsername());

        return userUrl.orElse(null);
    }

    public List<ImageIdUrlIsMainDto> saveTrailPictures(
            Long trailId,
            ImageCreateDto imageCreateDto,
            MultipartFile[] files,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailService.getTrailWithImagesAndImageCreatorByIdAndStatusIfOwner(
                        trailId,
                        statuses,
                        userDetails.getUsername()
                );
        UserEntity loggedUser = currentTrail.getCreatedBy();
        logger.info("Save trail pictures - logged user {}", loggedUser.getUsername());
        List<ImageEntity> currentTrailImages = currentTrail.getImages();

        int usedSlots = currentTrailImages.size();
        int neededImageSlots = files.length;
        int totalImages = usedSlots + neededImageSlots;

        validateImageSlots(totalImages, currentTrail.getMaxNumberOfImages());

        String folder = imageCreateDto.folder();
        List<Map<String, String>> uploadResults = validateUploadResult(files, folder);
        List<ImageEntity> newImageEntities = createMultipleImageEntities(uploadResults, loggedUser);

        List<ImageEntity> savedImages = this.imageRepository.saveAll(newImageEntities);

        if (currentTrailImages.isEmpty()) {
            currentTrail.setMainImage(savedImages.get(0));
        }

        currentTrailImages.addAll(newImageEntities);
        currentTrail.setTrailStatus(SuperUserReviewStatusEnum.PENDING);
        this.hikingTrailService.saveTrailWithoutReturn(currentTrail);

        return savedImages.stream()
                .map(e -> {
                    boolean isMain = e.getId().equals(currentTrail.getMainImage().getId());
                    return new ImageIdUrlIsMainDto(e.getId(), e.getImageUrl(), isMain);
                })
                .toList();
    }

    public boolean deleteTrailPicturesById(
            Long id,
            EntityIdsToDeleteDto toDeleteDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailService.getTrailWithImagesByIdIfOwner(id, userDetails.getUsername());

        deleteImagesFromTrail(currentTrail, toDeleteDto);

        return true;
    }

    public HikingTrailEntity deleteTrailPictureByEntity(
            HikingTrailEntity trailEntity,
            EntityIdsToDeleteDto toDeleteDto
    ) {
        return deleteImagesFromTrail(trailEntity, toDeleteDto);
    }

    private HikingTrailEntity deleteImagesFromTrail(
            HikingTrailEntity currentTrail,
            EntityIdsToDeleteDto toDeleteDto
    ) {
        Set<ImageEntity> imagesToDelete = getImagesToDelete(toDeleteDto, currentTrail);
        logger.info("Images to delete: {}", imagesToDelete);
        validateDeleteResult(imagesToDelete);

        currentTrail.getImages().removeAll(imagesToDelete);

        logger.info("Images successfully removed from currentTrail.");

        if (imagesToDelete.contains(currentTrail.getMainImage())) {
            if (!currentTrail.getImages().isEmpty()) {
                currentTrail.setMainImage(currentTrail.getImages().get(0));
                logger.info("Main image was deleted. New main image: {}", currentTrail.getMainImage());
            } else {
                currentTrail.setMainImage(null);
                logger.info("Main image was deleted and no other images exist. Main image set to null.");
            }
        }

        HikingTrailEntity savedTrail = this.hikingTrailService.saveTrailWithReturn(currentTrail);
        this.imageRepository.deleteAll(imagesToDelete);

        return savedTrail;
    }

    private void validateDeleteResult(Set<ImageEntity> imagesToDelete) {
        for (ImageEntity imageEntity : imagesToDelete) {
            String cloudId = imageEntity.getCloudId();
            String folder = imageEntity.getFolder();
            String deleteResult = this.cloudinaryService.deleteFile(cloudId, folder);

            if (!"ok".equals(deleteResult)) {
                throw new AppException("Failed to delete all images. Process aborted.",
                        HttpStatus.BAD_REQUEST);
            }
        }
    }

    private Set<ImageEntity> getImagesToDelete(
            EntityIdsToDeleteDto toDeleteDto,
            HikingTrailEntity currentTrail
    ) {
        List<ImageEntity> currentTrailImages = currentTrail.getImages();
        Set<Long> currentImageIds =
                currentTrailImages.stream()
                        .map(ImageEntity::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Long> validIds = new LinkedHashSet<>();
        List<Long> invalidIds = new ArrayList<>();

        for (Long imageId : toDeleteDto.ids()) {
            if (currentImageIds.contains(imageId)) {
                validIds.add(imageId);
            } else {
                invalidIds.add(imageId);
            }
        }

        if (!invalidIds.isEmpty()) {
            String kind = switch (toDeleteDto.folder()) {
                case "Trails" -> "trail";
                case "Accommodations" -> "accommodation";
                case "Destinations" -> "destination";
                default -> "";
            };

            throw new AppException("Images with IDs " + invalidIds + " not found in the " + kind + " entity!",
                    HttpStatus.BAD_REQUEST);
        }

        return currentTrailImages
                .stream()
                .filter(i -> validIds.contains(i.getId()))
                .collect(Collectors.toSet());
    }

    private ImageEntity createImageEntity(
            Map<String, String> cloudinaryResponse,
            UserEntity owner,
            boolean isProfileImage
    ) {
        String folder = cloudinaryResponse.get("folder");
        String url = cloudinaryResponse.get("url");
        String cloudinaryId = cloudinaryResponse.get("public_id");

        ImageEntity image = new ImageEntity();
        image.setImageUrl(url);
        image.setFolder(folder);
        image.setCloudId(cloudinaryId);
        if (isProfileImage) {
            image.setProfileOwner(owner);
            image.setStatus(StatusEnum.APPROVED);
        } else {
            image.setStatus(StatusEnum.PENDING);
        }
        image.setCreationDate(LocalDateTime.now());

        return image;
    }

    private List<ImageEntity> createMultipleImageEntities(
            List<Map<String, String>> results,
            UserEntity owner
    ) {
        return results.stream().map(r -> createImageEntity(r, owner, false)).collect(Collectors.toList());
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

    public void saveImagesWithoutReturn(List<ImageEntity> images) {
        this.imageRepository.saveAll(images);
    }
}
