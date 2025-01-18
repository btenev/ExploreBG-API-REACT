package bg.exploreBG.service;

import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicLikesDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.single.*;
import bg.exploreBG.model.dto.destination.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.DestinationMapper;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.utils.ImageUtils;
import bg.exploreBG.utils.OwnershipUtils;
import bg.exploreBG.utils.StatusValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class DestinationService {
    private final DestinationMapper mapper;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final GenericPersistenceService<CommentEntity> commentPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final LikeService likeService;
    private final CommentService commentService;
    private final EntityUpdateService entityUpdateService;
    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);

    public DestinationService(
            DestinationMapper destinationMapper,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            GenericPersistenceService<CommentEntity> commentPersistence,
            UserQueryBuilder userQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            LikeService likeService,
            CommentService commentService,
            EntityUpdateService entityUpdateService
    ) {
        this.mapper = destinationMapper;
        this.destinationPersistence = destinationPersistence;
        this.commentPersistence = commentPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.likeService = likeService;
        this.commentService = commentService;
        this.entityUpdateService = entityUpdateService;
    }

    public List<DestinationBasicDto> getRandomNumOfDestinations(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.destinationQueryBuilder.getRandomNumOfDestinations(pageable);
    }

    public DestinationDetailsDto getDestinationDetailsById(Long destinationId) {
        DestinationEntity destinationById = this.destinationQueryBuilder.getDestinationEntityById(destinationId);
        return this.mapper.destinationEntityToDestinationDetailsDto(destinationById);
    }

    @SuppressWarnings("unchecked")
    public <T> T getDestinationAuthenticated(Long destinationId, UserDetails userDetails) {
        DestinationEntity current = this.destinationQueryBuilder.getDestinationEntityById(destinationId);
        String username = userDetails.getUsername();

        if (OwnershipUtils.isOwner(current, username)) {
            logger.info("{} is owner of destination {}", username, current.getId());
            return (T) this.mapper.destinationEntityToDestinationDetailsDto(current);
        }

        StatusValidationUtils.ensureEntityIsApproved(current.getStatus(), "Destination");
        logger.info("Destination with id {} is approved", current.getId());

        List<ImageEntity> approvedImages = ImageUtils.filterByStatus(current.getImages(), StatusEnum.PENDING);

        if (approvedImages.size() != current.getImages().size()) {
            current.setImages(approvedImages);
        }

        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(username);
        return (T) this.mapper.destinationEntityToDestinationDetailsLikeDto(current, loggedUser);
    }

    public Page<DestinationBasicDto> getAllApprovedDestinations(Pageable pageable) {
        return this.destinationQueryBuilder.getAllDestinationsByStatus(pageable);
    }

    public List<DestinationIdAndDestinationNameDto> selectAll() {
        return this.destinationQueryBuilder.selectAllApprovedDestinations();
    }

    public DestinationIdDto createDestination(
            DestinationCreateOrReviewDto destinationCreateOrReviewDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        DestinationEntity newDestination =
                this.mapper.destinationCreateDtoToDestinationEntity(destinationCreateOrReviewDto);
        newDestination.setStatus(StatusEnum.PENDING);
        newDestination.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
        newDestination.setCreationDate(LocalDateTime.now());
        newDestination.setCreatedBy(validUser);

//        logger.debug("{}", newDestination);
        DestinationEntity saved = this.destinationPersistence.saveEntityWithReturn(newDestination);
        return new DestinationIdDto(saved.getId());
    }

    public List<DestinationBasicLikesDto> getRandomNumOfDestinationsWithLikes(UserDetails userDetails, int limit) {
        return this.destinationQueryBuilder.getRandomNumOfDestinationsLikes(userDetails.getUsername(), limit);
    }

    @Transactional
    public Page<DestinationBasicLikesDto> getAllApprovedDestinationsWithLikes(
            UserDetails userDetails,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        return this.destinationQueryBuilder
                .getAllDestinationsWithLikesByStatus(
                        StatusEnum.APPROVED, StatusEnum.APPROVED, userDetails.getUsername(), pageable, sortByLikedUser
                );
    }

    public boolean likeOrUnlikeDestinationAndSave(
            Long destinationId,
            LikeBooleanDto likeBoolean,
            UserDetails userDetails,
            StatusEnum status
    ) {
        DestinationEntity destination =
                this.destinationQueryBuilder.getDestinationWithLikesByIdAndStatus(destinationId, status);

        this.likeService.likeOrUnlikeEntity(destination, likeBoolean, userDetails);
        this.destinationPersistence.saveEntityWithoutReturn(destination);

        return true;
    }

    public CommentDto addDestinationComment(
            Long destinationId,
            CommentCreateDto commentCreate,
            UserDetails userDetails,
            StatusEnum status
    ) {

        return this.commentService.addComment(
                destinationId,
                status,
                commentCreate,
                userDetails,
                this.destinationQueryBuilder::getDestinationWithCommentsByIdAndStatus,
                this.destinationPersistence::saveEntityWithoutReturn);
    }

    public boolean deleteDestinationComment(
            Long destinationId,
            Long commentId,
            UserDetails userDetails
    ) {
        this.commentService.deleteComment(
                destinationId,
                commentId,
                userDetails,
                this.destinationQueryBuilder::getDestinationWithCommentsById,
                this.destinationPersistence::saveEntityWithoutReturn,
                ignored -> this.commentPersistence.deleteEntityWithoutReturnById(commentId));
        return true;
    }

    public DestinationNameDto updateDestinationName(
            Long destinationId,
            DestinationUpdateDestinationNameDto updateDestinationName,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getDestinationName,
                current::setDestinationName,
                updateDestinationName.destinationName(),
                (destination, isUpdated) -> new DestinationNameDto(
                        destination.getDestinationName(),
                        isUpdated ? destination.getModificationDate() : null));
    }

    public DestinationLocationDto updateLocation(
            Long destinationId,
            DestinationUpdateLocationDto updateLocation,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getLocation,
                current::setLocation,
                updateLocation.location(),
                (destination, isUpdated) -> new DestinationLocationDto(
                        destination.getLocation(),
                        isUpdated ? destination.getModificationDate() : null));
    }

    public DestinationInfoDto updateDestinationInfo(
            Long destinationId,
            DestinationUpdateInfoDto destinationUpdateInfo,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getDestinationInfo,
                current::setDestinationInfo,
                destinationUpdateInfo.destinationInfo(),
                (destination, isUpdated) -> new DestinationInfoDto(
                        destination.getDestinationInfo(),
                        isUpdated ? destination.getModificationDate() : null));
    }


    public DestinationNextToDto updateNextTo(
            Long destinationId,
            DestinationUpdateNextToDto updateNextTo,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getNextTo,
                current::setNextTo,
                updateNextTo.nextTo(),
                (destination, isUpdated) -> new DestinationNextToDto(
                        destination.getNextTo(),
                        isUpdated ? destination.getModificationDate() : null));
    }


    public DestinationTypeDto updateType(
            Long destinationId,
            DestinationUpdateTypeDto updateType,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getType,
                current::setType,
                updateType.type(),
                (destination, isUpdated) -> new DestinationTypeDto(
                        destination.getType().getValue(),
                        isUpdated ? destination.getModificationDate() : null));
    }

    public boolean updateDestinationMainImage(
            Long destinationId,
            ImageMainUpdateDto imageMainUpdate,
            UserDetails userDetails,
            List<StatusEnum> statusList
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationWithImagesByIdAndStatusIfOwner(
                                destinationId, statusList, userDetails.getUsername());

        ImageEntity found = ImageUtils.filterMainImage(current.getImages(), imageMainUpdate.imageId());

        boolean isUpdated = this.entityUpdateService
                .updateFieldIfDifferent(current::getMainImage, current::setMainImage, found);

        if (isUpdated) {
            this.destinationPersistence.saveEntityWithoutReturn(current);
        }

        return true;
    }

    private <T, R> R updateDestinationField(
            DestinationEntity destination,
            Supplier<T> getter,
            Consumer<T> setter,
            T newValue,
            BiFunction<DestinationEntity, Boolean, R> dtoMapper
    ) {
        boolean isUpdated = this.entityUpdateService.updateFieldIfDifferent(getter, setter, newValue);
        destination = updateDestinationStatusAndSaveIfChanged(destination, isUpdated);
        return dtoMapper.apply(destination, isUpdated);
    }

    private DestinationEntity updateDestinationStatusAndSaveIfChanged(
            DestinationEntity destination,
            boolean isUpdated
    ) {
        if (isUpdated) {
            destination.setStatus(StatusEnum.PENDING);
            destination.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
            destination.setModificationDate(LocalDateTime.now());
            destination = this.destinationPersistence.saveEntityWithReturn(destination);
        }
        return destination;
    }
}
