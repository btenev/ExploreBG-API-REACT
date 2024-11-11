package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationWrapperDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationWrapperDto;
import bg.exploreBG.model.dto.hikingTrail.*;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.entity.*;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.CommentMapper;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.repository.HikingTrailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class HikingTrailService {
    private static final Logger logger = LoggerFactory.getLogger(HikingTrailService.class);
    private final HikingTrailRepository hikingTrailRepository;
    private final HikingTrailMapper hikingTrailMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final CommentService commentService;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final EntityUpdateService entityUpdateService;

    public HikingTrailService(
            HikingTrailRepository hikingTrailRepository,
            HikingTrailMapper hikingTrailMapper,
            CommentMapper commentMapper,
            UserService userService,
            CommentService commentService,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            EntityUpdateService entityUpdateService
    ) {
        this.hikingTrailRepository = hikingTrailRepository;
        this.hikingTrailMapper = hikingTrailMapper;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.commentService = commentService;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.entityUpdateService = entityUpdateService;
    }

    public List<HikingTrailBasicDto> getRandomNumOfHikingTrails(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.hikingTrailRepository.findRandomApprovedTrails(pageable);
    }

    public List<HikingTrailBasicLikesDto> getRandomNumOfHikingTrailsWithLikes(
            int limit,
            UserDetails userDetails
    ) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.hikingTrailRepository.
                findRandomApprovedTrailsWithLikes(userDetails.getUsername(), pageable);
    }

    public HikingTrailDetailsDto getApprovedHikingTrailWithApprovedImagesById(Long id, StatusEnum status) {
        HikingTrailEntity trailById = getTrailByIdAndStatus(id, status);

        List<ImageEntity> approvedImages = getApprovedImages(trailById);

        if (approvedImages.size() != trailById.getImages().size()) {
            trailById.setImages(approvedImages);
        }

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(trailById);
    }

    @SuppressWarnings("unchecked")
    public <T> T getHikingTrailAuthenticated(Long id, UserDetails userDetails) {
        HikingTrailEntity current = getTrailById(id);
        String username = userDetails.getUsername();

        if (isOwner(current, username)) {
            logger.info("{} is owner of trail {}", username, current.getId());
            return (T) this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(current);
        }

        ensureTrailIsApproved(current);
        logger.info("Trail with id {} is approved", current.getId());

        List<ImageEntity> approvedImages = getApprovedImages(current);

        if (approvedImages.size() != current.getImages().size()) {
            current.setImages(approvedImages);
        }

        UserEntity loggedUser = this.userService.getUserEntityByEmail(username);
        return (T) this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsLikeDto(current, loggedUser);
    }

    private List<ImageEntity> getApprovedImages(HikingTrailEntity current) {
        return current.getImages().stream().filter(i -> i.getStatus().equals(StatusEnum.APPROVED)).toList();
    }

    private boolean isOwner(HikingTrailEntity trail, String username) {
        return trail.getCreatedBy() != null && trail.getCreatedBy().getEmail().equals(username);
    }

    private void ensureTrailIsApproved(HikingTrailEntity trail) {
        if (!trail.getStatus().equals(StatusEnum.APPROVED)) {
            throw new AppException("Hiking trail invalid status!", HttpStatus.BAD_REQUEST);
        }
    }

    public Page<HikingTrailBasicDto> getAllHikingTrails(Pageable pageable) {
        return this.hikingTrailRepository
                .findAllByTrailStatus(StatusEnum.APPROVED, pageable);
    }

    @Transactional
    public Page<HikingTrailBasicLikesDto> getAllHikingTrailsWithLikes(
            UserDetails userDetails,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        return this.hikingTrailRepository
                .getTrailsWithLikes(StatusEnum.APPROVED, userDetails.getUsername(), pageable, sortByLikedUser);
    }

    public List<HikingTrailIdTrailNameDto> selectAll() {
        return this.hikingTrailRepository.findAllBy();
    }

    public Long createHikingTrail(
            HikingTrailCreateOrReviewDto hikingTrailCreateOrReviewDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userService.getUserEntityByEmail(userDetails.getUsername());

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
        newHikingTrail.setTrailStatus(trailStatus);
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

        return saveTrailWithReturn(newHikingTrail).getId();
    }

    public HikingTrailStartPointDto updateHikingTrailStartPoint(
            Long trailId,
            HikingTrailUpdateStartPointDto newStartPoint,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
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
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
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
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
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
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
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
            Long id,
            HikingTrailUpdateWaterAvailableDto newWaterAvailable,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(id, userDetails.getUsername());
        return updateHikingTrailField(
                currentTrail,
                currentTrail::getWaterAvailable,
                currentTrail::setWaterAvailable,
                newWaterAvailable.waterAvailable(),
                (trail, isUpdated) -> new HikingTrailWaterAvailableDto(
                        trail.getWaterAvailable().getValue(),
                        isUpdated ? trail.getModificationDate() : null));
    }

    public HikingTrailActivityDto updateHikingTrailActivity(
            Long id,
            HikingTrailUpdateActivityDto newActivity,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(id, userDetails.getUsername());
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
            Long id,
            HikingTrailUpdateTrailInfoDto newTrailInfo,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(id, userDetails.getUsername());
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
        HikingTrailEntity currentTrail = getTrailWithHutsByIdAndStatusIfOwner(id, statuses, userDetails.getUsername());
        /*TODO: Test Object.equals with list, might need to change to set*/
        boolean isUpdated = this.entityUpdateService.updateAccommodationList(currentTrail, newHuts.availableHuts());

        currentTrail = updateTrailStatusAndSaveIfChanged(currentTrail, isUpdated);

        List<AccommodationBasicDto> availableHuts = currentTrail
                .getAvailableHuts()
                .stream()
                .map(hut -> new AccommodationBasicDto(hut.getId(), hut.getAccommodationName()))
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
        HikingTrailEntity currentTrail = getTrailWithDestinationsByAndStatusIfOwner(id, statuses, userDetails.getUsername());
        /*TODO: Test Object.equals with list, might need to change to set*/
        boolean isUpdated = this.entityUpdateService.updateDestinationList(currentTrail, newDestinations.destinations());

        currentTrail = updateTrailStatusAndSaveIfChanged(currentTrail, isUpdated);

        List<DestinationBasicDto> destinations = currentTrail
                .getDestinations()
                .stream()
                .map(destination -> new DestinationBasicDto(destination.getId(), destination.getDestinationName()))
                .collect(Collectors.toList());

        return new DestinationWrapperDto(
                destinations,
                isUpdated ? currentTrail.getModificationDate() : null
        );
    }

    public HikingTrailDifficultyDto updateHikingTrailDifficulty(
            Long id,
            HikingTrailUpdateTrailDifficultyDto newDifficulty,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = getTrailByIdAndStatusIfOwner(id, userDetails.getUsername());
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

        HikingTrailEntity currentTrail = getTrailWithImagesByIdAndStatusIfOwner(id, statuses, userDetails.getUsername());

        ImageEntity found =
                currentTrail
                        .getImages()
                        .stream()
                        .filter(i -> i.getId().equals(imageMainUpdateDto.imageId()))
                        .findFirst()
                        .orElseThrow(() ->
                                new AppException("Unable to update main image: The specified image is not part of the user's collection.",
                                        HttpStatus.BAD_REQUEST));

        boolean isUpdated =
                this.entityUpdateService
                        .updateFieldIfDifferent(currentTrail::getMainImage, currentTrail::setMainImage, found);

        if (isUpdated) {
            currentTrail.setMainImage(found);
            saveTrailWithoutReturn(currentTrail);
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
            trail.setTrailStatus(SuperUserReviewStatusEnum.PENDING);
            trail.setModificationDate(LocalDateTime.now());
            return saveTrailWithReturn(trail);
        }
        return trail;
    }

    public CommentDto addNewTrailComment(
            Long trailId,
            CommentCreateDto commentDto,
            UserDetails userDetails,
            StatusEnum status
    ) {
        HikingTrailEntity currentTrail = getTrailWithCommentsByIdAndStatus(trailId, status);

        UserEntity userCommenting = this.userService.getUserEntityByEmail(userDetails.getUsername());

        CommentEntity savedComment = this.commentService.saveComment(commentDto, userCommenting);

        currentTrail.setSingleComment(savedComment);
        saveTrailWithoutReturn(currentTrail);

        return this.commentMapper.commentEntityToCommentDto(savedComment);
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
        HikingTrailEntity currentTrail = getTrailWithCommentsById(trailId);

        this.commentService.validateCommentOwnership(commentId, userDetails.getUsername());

        boolean commentRemoved = currentTrail.getComments().removeIf(c -> c.getId().equals(commentId));

        if (!commentRemoved) {
            throw new AppException("Comment with id " + commentId + " was not found in the trail!",
                    HttpStatus.NOT_FOUND);
        }

        this.hikingTrailRepository.save(currentTrail);
        /*TODO: think how to handle exceptions*/
        this.commentService.deleteCommentById(commentId);

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getPendingApprovalTrailCount() {
        return this.hikingTrailRepository
                .countHikingTrailEntitiesByTrailStatus(SuperUserReviewStatusEnum.PENDING);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getUnderReviewTrailCount() {
        return this.hikingTrailRepository
                .countHikingTrailEntitiesByTrailStatus(SuperUserReviewStatusEnum.PENDING);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Page<HikingTrailForApprovalProjection> getAllHikingTrailsForApproval(
            SuperUserReviewStatusEnum status,
            Pageable pageable
    ) {
        return this.hikingTrailRepository
                .getHikingTrailEntitiesByTrailStatus(status, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerId(Long id) {
        Long reviewerId = this.hikingTrailRepository.findReviewerId(id);
        return new UserIdDto(reviewerId);
    }

    public HikingTrailEntity saveTrailWithReturn(HikingTrailEntity trail) {
        return this.hikingTrailRepository.save(trail);
    }

    public void saveTrailWithoutReturn(HikingTrailEntity trail) {
        this.hikingTrailRepository.save(trail);
    }

    public HikingTrailEntity getTrailById(Long trailId) {
        return this.hikingTrailQueryBuilder.getHikingTrailById(trailId);
    }

    public HikingTrailEntity getTrailWithCommentsById(Long trailId) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithCommentsById(trailId);
    }

    public HikingTrailEntity getTrailWithImagesById(Long trailId) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithImagesById(trailId);
    }

    public HikingTrailEntity getTrailByIdIfOwner(Long trailId, String email) {
        return this.hikingTrailQueryBuilder.getHikingTrailByIdIfOwner(trailId, email);
    }

    public HikingTrailEntity getTrailWithImagesByIdIfOwner(Long trailId, String email) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithImagesByIdIfOwner(trailId, email);
    }

    public HikingTrailEntity getTrailByIdAndStatusIfOwner(Long trailId, String email) {
        return this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatusIfOwner(trailId, email);
    }

    public HikingTrailEntity getTrailWithImagesByIdAndStatusIfOwner(
            Long trailId, List<StatusEnum> statuses, String email
    ) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithImagesByIdAndStatusIfOwner(trailId, statuses, email);
    }

    public HikingTrailEntity getTrailWithImagesAndImageCreatorByIdAndStatusIfOwner(
            Long trailId, List<StatusEnum> statuses, String email
    ) {
        return this.hikingTrailQueryBuilder
                .getHikingTrailWithImagesAndImageCreatorByIdAndStatusIfOwner(trailId, statuses, email);
    }

    public HikingTrailEntity getTrailWithDestinationsByAndStatusIfOwner(
            Long trailId, List<StatusEnum> statuses, String email
    ) {
        return this.hikingTrailQueryBuilder
                .getHikingTrailWithDestinationsByAndStatusIfOwner(trailId, statuses, email);
    }

    public HikingTrailEntity getTrailWithHutsByIdAndStatusIfOwner(
            Long trailId, List<StatusEnum> statuses, String email
    ) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithHutsByIdAndStatusIfOwner(trailId, statuses, email);
    }

    public HikingTrailEntity getTrailWithImagesByIdAndTrailStatus(Long trailId, SuperUserReviewStatusEnum status) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithImagesByIdAndTrailStatus(trailId, status);
    }

    public HikingTrailEntity getTrailWithCommentsByIdAndStatus(Long trailId, StatusEnum status) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithCommentsByIdAndStatus(trailId, status);
    }

    public HikingTrailEntity getTrailWithLikesByIdAndStatus(Long trailId, StatusEnum status) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithLikesByIdAndStatus(trailId, status);
    }

    private HikingTrailEntity getTrailByIdAndStatus(Long trailId, StatusEnum status) {
        return this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatus(trailId, status);
    }

    public HikingTrailEntity getTrailWithImagesAndImageReviewerAndGpxFileById(Long trailId) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithImagesAndImageReviewerAndGpxFileById(trailId);
    }

    public HikingTrailImageStatusAndGpxFileStatus getTrailImageStatusAndGpxFileStatus(Long trailId) {
        return this.hikingTrailQueryBuilder.getHikingTrailImageStatusAndGpxStatusById(trailId);
    }

    public HikingTrailEntity getTrailWithGpxFileById(Long trailId) {
        return this.hikingTrailQueryBuilder.getHikingTrailWithGpxFileById(trailId);
    }

    public boolean likeOrUnlikeTrail(
            Long trailId,
            LikeBooleanDto likeBoolean,
            UserDetails userDetails,
            StatusEnum status
    ) {
        HikingTrailEntity currentTrail = getTrailWithLikesByIdAndStatus(trailId, status);
        UserEntity loggedUser = userService.getUserEntityByEmail(userDetails.getUsername());
        Set<UserEntity> likedByUsers = currentTrail.getLikedByUsers();
        boolean userHasLiked = likedByUsers.contains(loggedUser);

        if (likeBoolean.like()) {
            handleLike(likedByUsers, loggedUser, userHasLiked);
        } else {
            handleUnlike(likedByUsers, loggedUser, userHasLiked);
        }

        saveTrailWithoutReturn(currentTrail);
        return true;
    }

    private void handleLike(Set<UserEntity> likedByUsers, UserEntity user, boolean userHasLiked) {
        if (userHasLiked) {
            throw new AppException("You have already liked the item!", HttpStatus.BAD_REQUEST);
        }
        likedByUsers.add(user);
    }

    private void handleUnlike(Set<UserEntity> likedByUsers, UserEntity user, boolean userHasLiked) {
        if (!userHasLiked) {
            throw new AppException("You cannot unlike an item that you haven't liked!", HttpStatus.BAD_REQUEST);
        }
        likedByUsers.remove(user);
    }
}
