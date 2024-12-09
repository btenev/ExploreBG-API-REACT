package bg.exploreBG.service;

import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicLikesDto;
import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
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

@Service
public class AccommodationService {
    private static final Logger logger = LoggerFactory.getLogger(AccommodationService.class);
    private final AccommodationMapper mapper;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final LikeService likeService;

    public AccommodationService(
            AccommodationMapper mapper,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            UserQueryBuilder userQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder,
            LikeService likeService
    ) {
        this.mapper = mapper;
        this.accommodationPersistence = accommodationPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.likeService = likeService;
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
            AccommodationCreateDto accommodationCreateDto,
            UserDetails userDetails
    ) {
        UserEntity verifiedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        AccommodationEntity newAccommodation =
                this.mapper.accommodationCreateDtoToAccommodationEntity(accommodationCreateDto);
        newAccommodation.setCreatedBy(verifiedUser);
        newAccommodation.setMaxNumberOfImages(10);
        newAccommodation.setCreationDate(LocalDateTime.now());
        newAccommodation.setStatus(StatusEnum.PENDING);
        newAccommodation.setAccommodationStatus(SuperUserReviewStatusEnum.PENDING);

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
            LikeBooleanDto likeBoolean,
            UserDetails userDetails,
            StatusEnum status
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder.getAccommodationWithLikesByIdAndStatus(accommodationId, status);

        this.likeService.likeOrUnlikeEntity(current, likeBoolean, userDetails);
        this.accommodationPersistence.saveEntityWithoutReturn(current);
        return true;
    }
}
