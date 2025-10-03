package bg.exploreBG.service;

import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicLikesDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.CommentMapper;
import bg.exploreBG.model.mapper.DestinationMapper;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.utils.ImageUtils;
import bg.exploreBG.utils.OwnershipUtils;
import bg.exploreBG.utils.PublicEntityUtils;
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
import java.util.Collection;
import java.util.List;

@Service
public class DestinationService {
    private final DestinationMapper mapper;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final GenericPersistenceService<CommentEntity> commentPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final LikeService likeService;
    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);

    public DestinationService(
            DestinationMapper destinationMapper,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            GenericPersistenceService<CommentEntity> commentPersistence,
            UserQueryBuilder userQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            LikeService likeService,
            CommentService commentService,
            CommentMapper commentMapper
    ) {
        this.mapper = destinationMapper;
        this.destinationPersistence = destinationPersistence;
        this.commentPersistence = commentPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.likeService = likeService;
        this.commentService = commentService;
        this.commentMapper = commentMapper;
    }

    public List<DestinationBasicDto> getRandomNumOfDestinations(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.destinationQueryBuilder.getRandomNumOfDestinations(pageable);
    }

    public DestinationDetailsDto getApprovedDestinationWithApprovedImagesById(
            Long destinationId,
            StatusEnum detailsStatus
    ) {
        return PublicEntityUtils.fetchAndMapWithApprovedImages(
                destinationId,
                detailsStatus,
                this.destinationQueryBuilder::getDestinationByIdAndStatus,
                this.mapper::destinationEntityToDestinationDetailsDto
        );
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
            LikeRequestDto dto,
            StatusEnum status
    ) {
        DestinationEntity destination =
                this.destinationQueryBuilder.getDestinationWithLikesByIdAndStatus(destinationId, status);

        this.likeService.likeOrUnlikeEntity(destination, dto);
        this.destinationPersistence.saveEntityWithoutReturn(destination);

        return dto.like();
    }

    public CommentDto addDestinationComment(
            Long destinationId,
            CommentRequestDto requestDto,
            UserDetails userDetails,
            StatusEnum status
    ) {
        return this.commentService.addComment(
                destinationId,
                requestDto,
                userDetails,
                this.destinationQueryBuilder::getDestinationWithCommentsByIdAndStatus,
                this.destinationPersistence::saveEntityWithoutReturn,
                status);
    }

    public void deleteDestinationComment(
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
                () -> this.commentPersistence.deleteEntityWithoutReturnById(commentId));
    }

    public List<CommentDto> getDestinationComments(Long destinationId) {
        List<CommentEntity> comments = this.destinationQueryBuilder.getDestinationCommentsById(destinationId);
        return this.commentMapper.commentEntityListToCommentDtoList(comments);
    }

    public List<DestinationEntity> mapDtoToDestinationEntities(Collection<DestinationIdDto> ids) {
        List<Long> destinationIds = ids.stream().map(DestinationIdDto::id).toList();
        return this.destinationQueryBuilder.getDestinationEntitiesByIdsAnStatus(destinationIds, StatusEnum.APPROVED);
    }

//    public boolean deleteOwnedDestinationById(
//            Long destinationId,
//            UserDetails userDetails
//    ) {
//       /* DestinationEntity currentDestination = this.destinationQueryBuilder*/
//
//        return false;
//    }
}
/*    public boolean deleteOwnedTrailById(
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


        this.trailPersistence.deleteEntityWithoutReturnById(trailId);

        return true;
                }*/