package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.EntityIdsToDeleteDto;
import bg.exploreBG.model.dto.image.ImageIdPlusUrlDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainDto;
import bg.exploreBG.model.dto.image.validate.ImageCreateDto;
import bg.exploreBG.model.entity.*;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.ownableEntity.OwnableEntity;
import bg.exploreBG.querybuilder.*;
import bg.exploreBG.reviewable.ReviewableWithImages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ImageService {
    private final Logger logger = LoggerFactory.getLogger(ImageService.class);
    private final CloudinaryService cloudinaryService;
    private final GenericPersistenceService<ImageEntity> imagePersistence;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final GenericPersistenceService<UserEntity> userPersistence;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final UserQueryBuilder userQueryBuilder;
    private final ImageQueryBuilder imageQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;

    public ImageService(
            CloudinaryService cloudinaryService,
            GenericPersistenceService<ImageEntity> imagePersistence,
            GenericPersistenceService<HikingTrailEntity> trailPersistence,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            GenericPersistenceService<UserEntity> userPersistence,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            UserQueryBuilder userQueryBuilder,
            ImageQueryBuilder imageQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder
    ) {
        this.cloudinaryService = cloudinaryService;
        this.imagePersistence = imagePersistence;
        this.trailPersistence = trailPersistence;
        this.accommodationPersistence = accommodationPersistence;
        this.userPersistence = userPersistence;
        this.destinationPersistence = destinationPersistence;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.userQueryBuilder = userQueryBuilder;
        this.imageQueryBuilder = imageQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
    }

    public ImageIdPlusUrlDto saveProfileImage(
            ImageCreateDto imageCreateDto,
            MultipartFile file,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());
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

        ImageEntity saved = this.imagePersistence.saveEntityWithReturn(userImage);

        loggedUser.setUserImage(saved);

        this.userPersistence.saveEntityWithoutReturn(loggedUser);

        return new ImageIdPlusUrlDto(saved.getId(), saved.getImageUrl());
    }

    public String getUserImageUrlByEmail(UserDetails userDetails) {
        return this.imageQueryBuilder.getImagerUrlByEmail(userDetails.getUsername());
    }

    public <T extends ReviewableWithImages & OwnableEntity> List<ImageIdUrlIsMainDto> saveEntityPictures(
            Long entityId,
            List<StatusEnum> statuses,
            UserDetails userDetails,
            ImageCreateDto imageCreateDto,
            MultipartFile[] files
    ) {
        T entity = fetchEntity(entityId, statuses, userDetails.getUsername(), imageCreateDto.folder());
        UserEntity entityCreator = entity.getCreatedBy();
        logger.info("Save accommodation pictures - logged user {}", entityCreator.getUsername());
        List<ImageEntity> entityImages = entity.getImages();

        int usedSlots = entityImages.size();
        int neededImageSlots = files.length;
        int totalImages = usedSlots + neededImageSlots;

        validateImageSlots(totalImages, entity.getMaxNumberOfImages());

        String folder = imageCreateDto.folder();
        List<Map<String, String>> uploadResults = validateUploadResult(files, folder);
        List<ImageEntity> newImageEntities = createMultipleImageEntities(uploadResults, entityCreator);

        List<ImageEntity> savedImages = this.imagePersistence.saveEntitiesWithReturn(newImageEntities);

        if (entityImages.isEmpty()) {
            entity.setMainImage(savedImages.get(0));
        }

        entityImages.addAll(newImageEntities);
        entity.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
        saveEntity(entity, folder);

        return savedImages.stream()
                .map(e -> {
                    boolean isMain = e.getId().equals(entity.getMainImage().getId());
                    return new ImageIdUrlIsMainDto(e.getId(), e.getImageUrl(), isMain);
                })
                .toList();
    }

    public void deleteTrailPicturesById(
            Long trailId,
            EntityIdsToDeleteDto toDeleteDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailWithImagesByIdIfOwner(trailId, userDetails.getUsername());
        /*TODO: return of the entity is not needed here, think about alternative solution*/
        deleteImagesFromEntityWithoutReturn(currentTrail, toDeleteDto, this.trailPersistence::saveEntityWithoutReturn);

    }

    public void deleteAccommodationPicturesById(
            Long accommodationId,
            EntityIdsToDeleteDto toDeleteDto,
            UserDetails userDetails
    ) {
        AccommodationEntity accommodation =
                this.accommodationQueryBuilder
                        .getAccommodationWithImagesByIdIfOwner(accommodationId, userDetails.getUsername());
        /*TODO: return of the entity is not needed here, think about alternative solution*/
        deleteImagesFromEntityWithoutReturn(accommodation, toDeleteDto, this.accommodationPersistence::saveEntityWithoutReturn);

    }

    private <T extends ReviewableWithImages & OwnableEntity> void saveEntity(T entity, String folder) {
        switch (folder.toLowerCase()) {
            case "trails", "trails-demo" -> this.trailPersistence.saveEntityWithoutReturn((HikingTrailEntity) entity);
            case "accommodations", "accommodations-demo" ->
                    this.accommodationPersistence.saveEntityWithoutReturn((AccommodationEntity) entity);
            case "destinations", "destinations-demo" ->
                    this.destinationPersistence.saveEntityWithoutReturn((DestinationEntity) entity);
            default -> throw new IllegalStateException("Unexpected value: " + folder.toLowerCase());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends ReviewableWithImages & OwnableEntity> T fetchEntity(
            Long entityId,
            List<StatusEnum> statuses,
            String username,
            String folder
    ) {
        return switch (folder.toLowerCase()) {
            case "trails", "trails-demo" -> (T) this.hikingTrailQueryBuilder
                    .getHikingTrailWithImagesAndImageCreatorByIdAndStatusIfOwner(entityId, statuses, username);
            case "accommodations", "accommodations-demo" -> (T) this.accommodationQueryBuilder
                    .getAccommodationWithImagesAndImageCreatorByIdAndStatusIfOwner(entityId, statuses, username);
            case "destinations", "destinations-demo" -> (T) this.destinationQueryBuilder
                    .getDestinationWithImagesAndImageCreatorByIdAndStatusIfOwner(entityId, statuses, username);
            default -> throw new AppException("Something went wrong", HttpStatus.BAD_REQUEST);
        };
    }

    public <T extends ReviewableWithImages> T deleteImagesFromEntityWithReturn(
            T entity,
            EntityIdsToDeleteDto toDeleteDto,
            Function<T, T> entitySaverWithReturn
    ) {
        return deleteImagesFromEntity(entity, toDeleteDto, null, entitySaverWithReturn, true);
    }

    public <T extends ReviewableWithImages> void deleteImagesFromEntityWithoutReturn(
            T entity,
            EntityIdsToDeleteDto toDeleteDto,
            Consumer<T> entitySaverWithoutReturn
    ) {
        deleteImagesFromEntity(entity, toDeleteDto, entitySaverWithoutReturn, null, false);
    }

    public <T extends ReviewableWithImages> void deleteAllImagesFromEntityWithoutReturn(
            T entity,
            Consumer<T> entitySaverWithoutReturn
    ) {
        List<ImageEntity> allEntityImageToDelete = new ArrayList<>( entity.getImages());
        logger.info("Images to delete: {}", allEntityImageToDelete);
        validateDeleteResult(new LinkedHashSet<>(allEntityImageToDelete));

        entity.getImages().removeAll(allEntityImageToDelete);
        logger.info("All images successfully removed from currentTrail.");
        entity.setMainImage(null);
        logger.info("Main image was deleted and no other images exist. Main image set to null.");

        entitySaverWithoutReturn.accept(entity);

        this.imagePersistence.deleteEntitiesWithoutReturn(allEntityImageToDelete);
    }

    private <T extends ReviewableWithImages> T deleteImagesFromEntity(
            T entity,
            EntityIdsToDeleteDto toDeleteDto,
            Consumer<T> entitySaverWithoutReturn,
            Function<T, T> entitySaverWithReturn,
            boolean withReturn
    ) {
        Set<ImageEntity> imagesToDelete = getImagesToDelete(toDeleteDto, entity.getImages());
        logger.info("Images to delete: {}", imagesToDelete);
        validateDeleteResult(imagesToDelete);

        entity.getImages().removeAll(imagesToDelete);
        logger.info("Images successfully removed from currentTrail.");

        if (imagesToDelete.contains(entity.getMainImage())) {
            if (!entity.getImages().isEmpty()) {
                entity.setMainImage(entity.getImages().get(0));
                logger.info("Main image was deleted. New main image: {}", entity.getMainImage());
            } else {
                entity.setMainImage(null);
                logger.info("Main image was deleted and no other images exist. Main image set to null.");
            }
        }

        if (withReturn) {
            entity = entitySaverWithReturn.apply(entity);
        } else {
            entitySaverWithoutReturn.accept(entity);
        }

        this.imagePersistence.deleteEntitiesWithoutReturn(imagesToDelete);

        return entity;
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
            EntityIdsToDeleteDto toDelete,
            List<ImageEntity> imageEntities
    ) {
        Set<Long> imageEntitiesIds =
                imageEntities.stream().map(ImageEntity::getId).collect(Collectors.toCollection(LinkedHashSet::new));

        Set<Long> validIds = new LinkedHashSet<>();
        List<Long> invalidIds = new ArrayList<>();

        for (Long imageId : toDelete.ids()) {
            if (imageEntitiesIds.contains(imageId)) {
                validIds.add(imageId);
            } else {
                invalidIds.add(imageId);
            }
        }

        if (!invalidIds.isEmpty()) {
            String kind = switch (toDelete.folder()) {
                case "Trails" -> "trail";
                case "Accommodations" -> "accommodation";
                case "Destinations" -> "destination";
                default -> "";
            };

            throw new AppException("Images with IDs " + invalidIds + " not found in the " + kind + " entity!",
                    HttpStatus.BAD_REQUEST);
        }

        return imageEntities
                .stream()
                .filter(imageEntity -> validIds.contains(imageEntity.getId()))
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
}
