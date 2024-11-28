package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AccommodationService {
    private static final Logger logger = LoggerFactory.getLogger(AccommodationService.class);
    private final AccommodationMapper mapper;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;

    public AccommodationService(
            AccommodationMapper mapper,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            UserQueryBuilder userQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder
    ) {
        this.mapper = mapper;
        this.accommodationPersistence = accommodationPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
    }

    public List<AccommodationBasicPlusImageDto> getRandomNumOfAccommodations(int limit) {
        long countOfAvailableAccommodations = this.accommodationQueryBuilder.getAccommodationCount();

        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfAvailableAccommodations);

        return this.accommodationQueryBuilder.getAccommodationsByIds(randomIds);
    }

    public AccommodationDetailsDto getAccommodationDetailsById(Long accommodationId) {
        AccommodationEntity accommodationEntityById =
                this.accommodationQueryBuilder.getAccommodationEntityById(accommodationId);

        return this.mapper.accommodationEntityToAccommodationDetailsDto(accommodationEntityById);
    }

    public Page<AccommodationBasicPlusImageDto> getAllAccommodations(Pageable pageable) {
        return this.accommodationQueryBuilder.getAllAccommodations(pageable);
    }

    public List<AccommodationBasicDto> selectAll() {
        return this.accommodationQueryBuilder.selectAllApprovedAccommodations();
    }

    public AccommodationIdDto createAccommodation(
            AccommodationCreateDto accommodationCreateDto,
            UserDetails userDetails
    ) {
        UserEntity verifiedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        AccommodationEntity newAccommodation =
                this.mapper.accommodationCreateDtoToAccommodationEntity(accommodationCreateDto);
        newAccommodation.setOwner(verifiedUser);
        newAccommodation.setAccommodationStatus(StatusEnum.PENDING);

        logger.debug("{}", newAccommodation);

        AccommodationEntity saved = this.accommodationPersistence.saveEntityWithReturn(newAccommodation);
        return new AccommodationIdDto(saved.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getPendingApprovalAccommodationCount() {
        return this.accommodationQueryBuilder.getAccommodationCountByStatus(StatusEnum.PENDING);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getUnderReviewAccommodationCount() {
        return this.accommodationQueryBuilder.getAccommodationCountByStatus(StatusEnum.REVIEW);
    }
}
