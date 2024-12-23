package bg.exploreBG.service;

import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicLikesDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateDto;
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

@Service
public class DestinationService {
    private final DestinationMapper mapper;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final LikeService likeService;
    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);

    public DestinationService(
            DestinationMapper destinationMapper,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            UserQueryBuilder userQueryBuilder, DestinationQueryBuilder destinationQueryBuilder,
            LikeService likeService
    ) {
        this.mapper = destinationMapper;
        this.destinationPersistence = destinationPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.likeService = likeService;
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
            DestinationCreateDto destinationCreateDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        DestinationEntity newDestination =
                this.mapper.destinationCreateDtoToDestinationEntity(destinationCreateDto);
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
}
