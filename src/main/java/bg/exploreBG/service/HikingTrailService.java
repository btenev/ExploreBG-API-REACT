package bg.exploreBG.service;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.entity.*;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.CommentMapper;
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
import java.util.function.Consumer;

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
    private final GenericDeleteService deleteService;
    private final ImageService imageService;
    private final CommentMapper commentMapper;

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
            GenericDeleteService deleteService,
            ImageService imageService,
            CommentMapper commentMapper) {
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
        this.imageService = imageService;
        this.commentMapper = commentMapper;
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

    public List<CommentDto> getHikingTrailComments(Long trailId) {
        List<CommentEntity> comments = this.hikingTrailQueryBuilder.getHikingTrailCommentsById(trailId);
        return this.commentMapper.commentEntityListToCommentDtoList(comments);
    }


    public CommentDto addNewTrailComment(
            Long trailId,
            CommentRequestDto commentDto,
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

    To delete a ChildEntity, you can remove it from the collection in the ParentEntity and then save the ParentEntity.
    The removed ChildEntity will be deleted from the database due to the cascading delete operation.
    */

    public void deleteTrailComment(
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

    public void deleteOwnedHikingTrailById(
            Long trailId,
            UserDetails userDetails
    ) {
        HikingTrailEntity trail = this.hikingTrailQueryBuilder.getHikingTrailByIdIfOwner(trailId, userDetails.getUsername());

        deleteService.deleteEntityWithOwnershipCheck(
                trailId,
                trail,
                preDeleteHookForHikingTrail(),
                trailPersistence::deleteEntityWithoutReturnById
        );
    }

    private Consumer<HikingTrailEntity> preDeleteHookForHikingTrail() {
        return deleteService.buildPreDeleteHook(
                true,
                true,
                this.trailPersistence::saveEntityWithoutReturn,
                this.hikePersistence::saveEntitiesWithoutReturn,
                this.imageService::deleteAllImagesFromEntityWithoutReturn
        );
    }

}
