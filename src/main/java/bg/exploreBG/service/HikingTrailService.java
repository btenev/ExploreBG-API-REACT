package bg.exploreBG.service;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.AccommodationWrapperDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.DestinationWrapperDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.*;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.querybuilder.HikeQueryBuilder;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.utils.ImageUtils;
import bg.exploreBG.utils.OwnershipUtils;
import bg.exploreBG.utils.StatusValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class HikingTrailService {
    private static final Logger logger = LoggerFactory.getLogger(HikingTrailService.class);
    private final HikingTrailMapper hikingTrailMapper;
    private final UserService userService;
    private final CommentService commentService;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final EntityUpdateService entityUpdateService;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;
    private final GenericPersistenceService<CommentEntity> commentPersistence;
    private final GenericPersistenceService<HikeEntity> hikePersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final HikeQueryBuilder hikeQueryBuilder;
    private final LikeService likeService;
    private final EntityDeleteService deleteService;

    public HikingTrailService(
            HikingTrailMapper hikingTrailMapper,
            UserService userService,
            CommentService commentService,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            EntityUpdateService entityUpdateService,
            GenericPersistenceService<HikingTrailEntity> trailPersistence,
            GenericPersistenceService<CommentEntity> commentPersistence,
            GenericPersistenceService<HikeEntity> hikePersistence,
            UserQueryBuilder userQueryBuilder,
            HikeQueryBuilder hikeQueryBuilder,
            LikeService likeService,
            EntityDeleteService deleteService
    ) {
        this.hikingTrailMapper = hikingTrailMapper;
        this.userService = userService;
        this.commentService = commentService;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.entityUpdateService = entityUpdateService;
        this.trailPersistence = trailPersistence;
        this.commentPersistence = commentPersistence;
        this.hikePersistence = hikePersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.hikeQueryBuilder = hikeQueryBuilder;
        this.likeService = likeService;
        this.deleteService = deleteService;
    }

    public List<HikingTrailBasicDto> getRandomNumOfHikingTrails(int limit) {
        return this.hikingTrailQueryBuilder.getRandomNumOfHikingTrails(limit);
    }

    public List<HikingTrailBasicLikesDto> getRandomNumOfHikingTrailsWithLikes(int limit, UserDetails userDetails) {
        return this.hikingTrailQueryBuilder.getRandomNumOfHikingTrailsWithLikes(userDetails.getUsername(), limit);
    }

    public HikingTrailDetailsDto getApprovedHikingTrailWithApprovedImagesById(Long id, StatusEnum status) {
        HikingTrailEntity trailById = this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatus(id, status);

        List<ImageEntity> approvedImages = ImageUtils.filterByStatus(trailById.getImages(), StatusEnum.APPROVED);

        if (approvedImages.size() != trailById.getImages().size()) {
            trailById.setImages(approvedImages);
        }

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(trailById);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHikingTrailAuthenticated(Long id, UserDetails userDetails) {
        HikingTrailEntity current = this.hikingTrailQueryBuilder.getHikingTrailById(id);
        String username = userDetails.getUsername();

        if (OwnershipUtils.isOwner(current, username)) {
            logger.info("{} is owner of trail {}", username, current.getId());
            return (T) this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(current);
        }

        StatusValidationUtils.ensureEntityIsApproved(current.getStatus(), "Trail");
        logger.info("Trail with id {} is approved", current.getId());

        List<ImageEntity> approvedImages = ImageUtils.filterByStatus(current.getImages(), StatusEnum.APPROVED);

        if (approvedImages.size() != current.getImages().size()) {
            current.setImages(approvedImages);
        }

        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(username);
        return (T) this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsLikeDto(current, loggedUser);
    }

    public void deleteOwnedTrailById(
            Long trailId,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailWithHikesByIdIfOwner(trailId, userDetails.getUsername());

        if (!currentTrail.getHikes().isEmpty()) {
            for (HikeEntity hike : currentTrail.getHikes()) {
                hike.setHikingTrail(null);
            }

            this.hikePersistence.saveEntitiesWithoutReturn(currentTrail.getHikes());
        }

        this.deleteService.deleteEntity(trailId, userDetails, this.hikingTrailQueryBuilder::getHikingTrailByIdIfOwner);

        /*Alternative of the code above without fetching any entities, but directly updating the database*//*
        this.hikeQueryBuilder.removeHikingTrailFromHikesByTrailIdIfTrailOwner(trailId, userDetails.getUsername());*/

        this.trailPersistence.deleteEntityWithoutReturnById(trailId);
    }

    public Page<HikingTrailBasicDto> getAllApprovedHikingTrails(Pageable pageable) {
        return this.hikingTrailQueryBuilder.getAllAHikingTrailsByStatus(StatusEnum.APPROVED, pageable);
    }

    @Transactional
    public Page<HikingTrailBasicLikesDto> getAllApprovedHikingTrailsWithLikes(
            UserDetails userDetails,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        return this.hikingTrailQueryBuilder
                .getAllHikingTrailsWithLikesByStatus(
                        StatusEnum.APPROVED, userDetails.getUsername(), pageable, sortByLikedUser);
    }

    public List<HikingTrailIdTrailNameDto> selectAll() {
        return this.hikingTrailQueryBuilder.selectAll();
    }

    public Long createHikingTrail(
            HikingTrailCreateOrReviewDto hikingTrailCreateOrReviewDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        HikingTrailEntity newHikingTrail =
                this.hikingTrailMapper
                        .hikingTrailCreateDtoToHikingTrailEntity(hikingTrailCreateOrReviewDto);

//        logger.debug("{}", newHikingTrail);
        boolean superUser = this.userService.isSuperUser(userDetails);

        StatusEnum status = superUser ? StatusEnum.APPROVED : StatusEnum.PENDING;

        SuperUserReviewStatusEnum trailStatus =
                superUser ? SuperUserReviewStatusEnum.APPROVED : SuperUserReviewStatusEnum.PENDING;

        if (superUser) {
            newHikingTrail.setReviewedBy(validUser);
        }

        newHikingTrail.setStatus(status);
        newHikingTrail.setEntityStatus(trailStatus);
        newHikingTrail.setMaxNumberOfImages(10);
        newHikingTrail.setCreatedBy(validUser);
        newHikingTrail.setCreationDate(LocalDateTime.now());

        if (!hikingTrailCreateOrReviewDto.destinations().isEmpty()) {
            List<DestinationEntity> destinationEntities =
                    this.entityUpdateService.mapDtoToDestinationEntities(hikingTrailCreateOrReviewDto.destinations());
            newHikingTrail.setDestinations(destinationEntities);
        }

        if (!hikingTrailCreateOrReviewDto.availableHuts().isEmpty()) {
            List<AccommodationEntity> accommodationEntities =
                    this.entityUpdateService.mapDtoToAccommodationEntities(hikingTrailCreateOrReviewDto.availableHuts());
            newHikingTrail.setAvailableHuts(accommodationEntities);
        }
        newHikingTrail = this.trailPersistence.saveEntityWithReturn(newHikingTrail);
        logger.info("Hiking trail with id {} is created", newHikingTrail.getId());
        return newHikingTrail.getId();
    }

    public HikingTrailStartPointDto updateHikingTrailStartPoint(
            Long trailId,
            HikingTrailUpdateStartPointDto newStartPoint,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());

        return updateHikingTrailField(
                currentTrail,
                currentTrail::getStartPoint,
                currentTrail::setStartPoint,
                newStartPoint.startPoint(),
                (trail, isUpdated) -> new HikingTrailStartPointDto(
                        trail.getStartPoint(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public HikingTrailEndPointDto updateHikingTrailEndPoint(
            Long trailId,
            HikingTrailUpdateEndPointDto newEndPoint,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getEndPoint,
                currentTrail::setEndPoint,
                newEndPoint.endPoint(),
                (trail, isUpdated) -> new HikingTrailEndPointDto(
                        trail.getEndPoint(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public HikingTrailTotalDistanceDto updateHikingTrailTotalDistance(
            Long trailId,
            HikingTrailUpdateTotalDistanceDto newTotalDistance,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getTotalDistance,
                currentTrail::setTotalDistance,
                newTotalDistance.totalDistance(),
                (trail, isUpdated) -> new HikingTrailTotalDistanceDto(
                        trail.getTotalDistance(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public HikingTrailElevationGainedDto updateHikingTrailElevationGained(
            Long trailId,
            HikingTrailUpdateElevationGainedDto newElevationGained,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getElevationGained,
                currentTrail::setElevationGained,
                newElevationGained.elevationGained(),
                (trail, isUpdated) -> new HikingTrailElevationGainedDto(
                        trail.getElevationGained(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public HikingTrailWaterAvailableDto updateHikingTrailWaterAvailable(
            Long trailId,
            HikingTrailUpdateWaterAvailableDto newWaterAvailable,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getWaterAvailability,
                currentTrail::setWaterAvailability,
                newWaterAvailable.waterAvailable(),
                (trail, isUpdated) -> new HikingTrailWaterAvailableDto(
                        trail.getWaterAvailability().getValue(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public HikingTrailActivityDto updateHikingTrailActivity(
            Long trailId,
            HikingTrailUpdateActivityDto newActivity,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        /*TODO: Test Object.equals with list, might need to change to set*/
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getActivity,
                currentTrail::setActivity,
                newActivity.activity(),
                (trail, isUpdated) -> new HikingTrailActivityDto(
                        trail.getActivity().stream().map(SuitableForEnum::getValue).collect(Collectors.toList()),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public HikingTrailTrailInfoDto updateHikingTrailTrailInfo(
            Long trailId,
            HikingTrailUpdateTrailInfoDto newTrailInfo,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getTrailInfo,
                currentTrail::setTrailInfo,
                newTrailInfo.trailInfo(),
                (trail, isUpdated) -> new HikingTrailTrailInfoDto(
                        trail.getTrailInfo(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public AccommodationWrapperDto updateHikingTrailAvailableHuts(
            Long id,
            HikingTrailUpdateAvailableHutsDto newHuts,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithHutsByIdAndStatusIfOwner(id, statuses, userDetails.getUsername());
        /*TODO: Test Object.equals with list, might need to change to set*/
        boolean isUpdated = this.entityUpdateService.updateAccommodationList(currentTrail, newHuts.availableHuts());

        currentTrail = updateTrailStatusAndSaveIfChanged(currentTrail, isUpdated);

        List<AccommodationIdAndAccommodationName> availableHuts = currentTrail
                .getAvailableHuts()
                .stream()
                .map(hut -> new AccommodationIdAndAccommodationName(hut.getId(), hut.getAccommodationName()))
                .collect(Collectors.toList());

        return new AccommodationWrapperDto(
                availableHuts,
                isUpdated ? currentTrail.getModificationDate() : null);
    }

    public DestinationWrapperDto updateHikingTrailDestinations(
            Long id,
            HikingTrailUpdateDestinationsDto newDestinations,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithDestinationsByAndStatusIfOwner(id, statuses, userDetails.getUsername());
        /*TODO: Test Object.equals with list, might need to change to set*/
        boolean isUpdated = this.entityUpdateService.updateDestinationList(currentTrail, newDestinations.destinations());

        currentTrail = updateTrailStatusAndSaveIfChanged(currentTrail, isUpdated);

        List<DestinationIdAndDestinationNameDto> destinations = currentTrail
                .getDestinations()
                .stream()
                .map(destination -> new DestinationIdAndDestinationNameDto(destination.getId(), destination.getDestinationName()))
                .collect(Collectors.toList());

        return new DestinationWrapperDto(
                destinations,
                isUpdated ? currentTrail.getModificationDate() : null
        );
    }

    public HikingTrailDifficultyDto updateHikingTrailDifficulty(
            Long trailId,
            HikingTrailUpdateTrailDifficultyDto newDifficulty,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getTrailDifficulty,
                currentTrail::setTrailDifficulty,
                newDifficulty.trailDifficulty(),
                (trail, isUpdated) -> new HikingTrailDifficultyDto(
                        trail.getTrailDifficulty().getLevel(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public boolean updateHikingTrailMainImage(
            Long id,
            ImageMainUpdateDto imageMainUpdateDto,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithImagesByIdAndStatusIfOwner(id, statuses, userDetails.getUsername());

        ImageEntity found = ImageUtils.filterMainImage(currentTrail.getImages(), imageMainUpdateDto.imageId());

        boolean isUpdated =
                this.entityUpdateService
                        .updateFieldIfDifferent(currentTrail::getMainImage, currentTrail::setMainImage, found);

        if (isUpdated) {
            currentTrail.setMainImage(found);
            this.trailPersistence.saveEntityWithoutReturn(currentTrail);
        }

        return true;
    }

    private <T, R> R updateHikingTrailField(
            HikingTrailEntity trail,
            Supplier<T> getter,
            Consumer<T> setter,
            T newValue,
            BiFunction<HikingTrailEntity, Boolean, R> dtoMapper
    ) {
        boolean isUpdated = this.entityUpdateService.updateFieldIfDifferent(getter, setter, newValue);
        trail = updateTrailStatusAndSaveIfChanged(trail, isUpdated);
        return dtoMapper.apply(trail, isUpdated);
    }

    private HikingTrailEntity updateTrailStatusAndSaveIfChanged(
            HikingTrailEntity trail,
            boolean isUpdated
    ) {
        if (isUpdated) {
            trail.setStatus(StatusEnum.PENDING);
            trail.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
            trail.setModificationDate(LocalDateTime.now());
            trail = this.trailPersistence.saveEntityWithReturn(trail);
        }
        return trail;
    }

    public CommentDto addNewTrailComment(
            Long trailId,
            CommentCreateDto commentDto,
            UserDetails userDetails,
            StatusEnum status
    ) {
        return this.commentService.addComment(
                trailId,
                status,
                commentDto,
                userDetails,
                this.hikingTrailQueryBuilder::getHikingTrailWithCommentsByIdAndStatus,
                this.trailPersistence::saveEntityWithoutReturn
        );
    }

    /*
    In this example, the ParentEntity has a list of ChildEntity objects. The @OneToMany annotation with the cascade = CascadeType.ALL
    attribute means that any operation (including deletion) performed on the ParentEntity will be cascaded to the ChildEntity objects.
    The orphanRemoval = true attribute ensures that if a ChildEntity object is removed from the collection, it will be deleted from the database.

    To delete a ChildEntity, you can simply remove it from the collection in the ParentEntity and then save the ParentEntity.
    The removed ChildEntity will be deleted from the database due to the cascading delete operation.
    */

    public boolean deleteTrailComment(
            Long trailId,
            Long commentId,
            UserDetails userDetails
    ) {
        this.commentService
                .deleteComment(
                        trailId,
                        commentId,
                        userDetails,
                        this.hikingTrailQueryBuilder::getHikingTrailWithCommentsById,
                        this.trailPersistence::saveEntityWithoutReturn,
                        ignored -> this.commentPersistence.deleteEntityWithoutReturnById(commentId)
                );
        return true;
    }

    public boolean likeOrUnlikeTrailAndSave(
            Long trailId,
            LikeRequestDto likeBoolean,
            StatusEnum status
    ) {
        HikingTrailEntity trail =
                this.hikingTrailQueryBuilder.getHikingTrailWithLikesByIdAndStatus(trailId, status);

        likeService.likeOrUnlikeEntity(trail, likeBoolean);
        this.trailPersistence.saveEntityWithoutReturn(trail);
        return likeBoolean.like();
    }
}
