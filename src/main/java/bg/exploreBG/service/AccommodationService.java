package bg.exploreBG.service;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicLikesDto;
import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.single.*;
import bg.exploreBG.model.dto.accommodation.validate.*;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.model.mapper.CommentMapper;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
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
public class AccommodationService {
    private static final Logger logger = LoggerFactory.getLogger(AccommodationService.class);
    private final AccommodationMapper mapper;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final LikeService likeService;
    private final EntityUpdateService entityUpdateService;
    private final CommentService commentService;
    private final GenericPersistenceService<CommentEntity> commentPersistence;
    private final CommentMapper commentMapper;

    public AccommodationService(
            AccommodationMapper accommodationMapper,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            UserQueryBuilder userQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder,
            LikeService likeService,
            EntityUpdateService entityUpdateService,
            CommentService commentService,
            GenericPersistenceService<CommentEntity> commentPersistence,
            CommentMapper commentMapper
    ) {
        this.mapper = accommodationMapper;
        this.accommodationPersistence = accommodationPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.likeService = likeService;
        this.entityUpdateService = entityUpdateService;
        this.commentService = commentService;
        this.commentPersistence = commentPersistence;
        this.commentMapper = commentMapper;
    }

    public List<AccommodationBasicDto> getRandomNumOfAccommodations(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.accommodationQueryBuilder.getRandomNumOfAccommodations(pageable);
    }

    public AccommodationDetailsDto getAccommodationDetailsById(Long accommodationId) {
        AccommodationEntity accommodationEntityById =
                this.accommodationQueryBuilder.getAccommodationEntityById(accommodationId);

        return this.mapper.accommodationEntityToAccommodationDetailsDto(accommodationEntityById);
    }

    public Page<AccommodationBasicDto> getAllApprovedAccommodations(Pageable pageable) {
        return this.accommodationQueryBuilder.getAllAccommodationsByStatus(StatusEnum.APPROVED, pageable);
    }

    public List<AccommodationIdAndAccommodationName> selectAll() {
        return this.accommodationQueryBuilder.selectAllApprovedAccommodations();
    }

    public AccommodationIdDto createAccommodation(
            AccommodationCreateOrReviewDto accommodationCreateOrReviewDto,
            UserDetails userDetails
    ) {
        UserEntity verifiedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        AccommodationEntity newAccommodation =
                this.mapper.accommodationCreateDtoToAccommodationEntity(accommodationCreateOrReviewDto);
        newAccommodation.setCreatedBy(verifiedUser);
        newAccommodation.setMaxNumberOfImages(10);
        newAccommodation.setCreationDate(LocalDateTime.now());
        newAccommodation.setStatus(StatusEnum.PENDING);
        newAccommodation.setEntityStatus(SuperUserReviewStatusEnum.PENDING);

        logger.info("Create new accommodation {}", newAccommodation);

        AccommodationEntity saved = this.accommodationPersistence.saveEntityWithReturn(newAccommodation);
        return new AccommodationIdDto(saved.getId());
    }

    public List<AccommodationBasicLikesDto> getRandomNumOfAccommodationsWithLikes(UserDetails userDetails, int limit) {
        return this.accommodationQueryBuilder.getRandomNumOfAccommodationLikes(userDetails.getUsername(), limit);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAccommodationAuthenticated(Long accommodationId, UserDetails userDetails) {
        AccommodationEntity current = this.accommodationQueryBuilder.getAccommodationEntityById(accommodationId);
        String username = userDetails.getUsername();

        if (OwnershipUtils.isOwner(current, username)) {
            logger.info("{} is owner of accommodation {}", username, current.getId());
            return (T) this.mapper.accommodationEntityToAccommodationDetailsDto(current);
        }

        StatusValidationUtils.ensureEntityIsApproved(current.getStatus(), "Accommodation");
        logger.info("Accommodation with id {} is approved", current.getId());

        List<ImageEntity> approvedImages = ImageUtils.filterByStatus(current.getImages(), StatusEnum.APPROVED);

        if (approvedImages.size() != current.getImages().size()) {
            current.setImages(approvedImages);
        }

        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(username);
        return (T) this.mapper.accommodationEntityToAccommodationWithLikesDto(current, loggedUser);
    }

    @Transactional
    public Page<AccommodationBasicLikesDto> getAllApprovedAccommodationsWithLikes(
            UserDetails userDetails,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        return this.accommodationQueryBuilder
                .getAllAccommodationsWithLikesByStatus(
                        StatusEnum.APPROVED, StatusEnum.APPROVED, userDetails.getUsername(), pageable, sortByLikedUser
                );
    }

    public boolean likeOrUnlikeAccommodationAndSave(
            Long accommodationId,
            LikeRequestDto dto,
            StatusEnum status
    ) {
        AccommodationEntity accommodation =
                this.accommodationQueryBuilder.getAccommodationWithLikesByIdAndStatus(accommodationId, status);

        this.likeService.likeOrUnlikeEntity(accommodation, dto);
        this.accommodationPersistence.saveEntityWithoutReturn(accommodation);

        return dto.like();
    }

    public AccommodationNameDto updateAccommodationName(
            Long accommodationId,
            AccommodationUpdateAccommodationNameDto updateAccommodationName,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAccommodationName,
                current::setAccommodationName,
                updateAccommodationName.accommodationName(),
                (accommodation, isUpdated) -> new AccommodationNameDto(
                        accommodation.getAccommodationName(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationPhoneNumberDto updateAccommodationPhoneNumber(
            Long accommodationId,
            AccommodationUpdatePhoneNumberDto updatePhoneNumber,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getPhoneNumber,
                current::setPhoneNumber,
                updatePhoneNumber.phoneNumber(),
                (accommodation, isUpdated) -> new AccommodationPhoneNumberDto(
                        accommodation.getPhoneNumber(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationSiteDto updateAccommodationSite(
            Long accommodationId,
            AccommodationUpdateSiteDto updateSite,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getSite,
                current::setSite,
                updateSite.site(),
                (accommodation, isUpdated) -> new AccommodationSiteDto(
                        accommodation.getSite(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }


    public AccommodationInfoDto updateAccommodationInfo(
            Long accommodationId,
            AccommodationUpdateInfoDto updateInfo,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAccommodationInfo,
                current::setAccommodationInfo,
                updateInfo.accommodationInfo(),
                (accommodation, isUpdated) -> new AccommodationInfoDto(
                        accommodation.getAccommodationInfo(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }


    public AccommodationBedCapacityDto updateAccommodationBedCapacity(
            Long accommodationId,
            AccommodationUpdateBedCapacityDto updateBedCapacity,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getBedCapacity,
                current::setBedCapacity,
                updateBedCapacity.bedCapacity(),
                (accommodation, isUpdated) -> new AccommodationBedCapacityDto(
                        accommodation.getBedCapacity(),
                        isUpdated ? accommodation.getModificationDate() : null
                )
        );
    }

    public AccommodationAvailableFoodDto updateAccommodationAvailableFood(
            Long accommodationId,
            AccommodationUpdateAvailableFoodDto updateAvailableFood,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAvailableFood,
                current::setAvailableFood,
                updateAvailableFood.availableFood(),
                (accommodation, isUpdated) -> new AccommodationAvailableFoodDto(
                        accommodation.getAvailableFood().getValue(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationPricePerBedDto updateAccommodationPricePerBed(
            Long accommodationId,
            AccommodationUpdatePricePerBed updatePricePerBed,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getPricePerBed,
                current::setPricePerBed,
                updatePricePerBed.pricePerBed(),
                (accommodation, isUpdated) -> new AccommodationPricePerBedDto(
                        accommodation.getPricePerBed(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationAccessibilityDto updateAccommodationAccessibility(
            Long accommodationId,
            AccommodationUpdateAccessibilityDto accessibility,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAccess,
                current::setAccess,
                accessibility.access(),
                (accommodation, isUpdated) -> new AccommodationAccessibilityDto(
                        accommodation.getAccess().getValue(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationNextToDto updateAccommodationNextTo(
            Long accommodationId,
            AccommodationUpdateNextToDto nextTo,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getNextTo,
                current::setNextTo,
                nextTo.nextTo(),
                (accommodation, isUpdated) -> new AccommodationNextToDto(
                        accommodation.getNextTo(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationTypeDto updateAccommodationType(
            Long accommodationId,
            AccommodationUpdateTypeDto accommodationType,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getType,
                current::setType,
                accommodationType.type(),
                (accommodation, isUpdated) -> new AccommodationTypeDto(
                        accommodation.getType().getValue(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    private <T, R> R updateAccommodationField(
            AccommodationEntity accommodation,
            Supplier<T> getter,
            Consumer<T> setter,
            T newValue,
            BiFunction<AccommodationEntity, Boolean, R> dtoMapper
    ) {
        boolean isUpdated = this.entityUpdateService.updateFieldIfDifferent(getter, setter, newValue);
        accommodation = updateAccommodationStatusAndSaveIfChanged(accommodation, isUpdated);
        return dtoMapper.apply(accommodation, isUpdated);
    }

    private AccommodationEntity updateAccommodationStatusAndSaveIfChanged(
            AccommodationEntity accommodation,
            boolean isUpdated
    ) {
        if (isUpdated) {
            accommodation.setStatus(StatusEnum.PENDING);
            accommodation.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
            accommodation.setModificationDate(LocalDateTime.now());
            accommodation = this.accommodationPersistence.saveEntityWithReturn(accommodation);
        }
        return accommodation;
    }

    public long updateAccommodationMainImage(
            Long accommodationId,
            ImageMainUpdateDto dto,
            UserDetails userDetails,
            List<StatusEnum> statusList
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationWithImagesByIdAndStatusIfOwner(
                                accommodationId, statusList, userDetails.getUsername());

        ImageEntity found = ImageUtils.filterMainImage(current.getImages(), dto.imageId());

        boolean isUpdated =
                this.entityUpdateService
                        .updateFieldIfDifferent(current::getMainImage, current::setMainImage, found);

        if (isUpdated) {
            this.accommodationPersistence.saveEntityWithoutReturn(current);
        }

        return found.getId();
    }

    public List<CommentDto> getAccommodationComments(Long accommodationId) {
        List<CommentEntity> comments = this.accommodationQueryBuilder.getAccommodationCommentsById(accommodationId);
        return this.commentMapper.commentEntityListToCommentDtoList(comments);
    }

    public CommentDto addAccommodationComment(
            Long accommodationId,
            CommentRequestDto requestDto,
            UserDetails userDetails,
            StatusEnum status
    ) {
        return this.commentService.addComment(
                accommodationId,
                status,
                requestDto,
                userDetails,
                this.accommodationQueryBuilder::getAccommodationWithCommentsByIdAndStatus,
                this.accommodationPersistence::saveEntityWithoutReturn);
    }

    public void deleteAccommodationComment(
            Long accommodationId,
            Long commentId,
            UserDetails userDetails
    ) {
        this.commentService
                .deleteComment(
                        accommodationId,
                        commentId,
                        userDetails,
                        this.accommodationQueryBuilder::getAccommodationWithCommentsById,
                        this.accommodationPersistence::saveEntityWithoutReturn,
                        ignored -> this.commentPersistence.deleteEntityWithoutReturnById(commentId));
    }
}

