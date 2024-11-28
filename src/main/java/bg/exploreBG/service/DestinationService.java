package bg.exploreBG.service;

import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.mapper.DestinationMapper;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.repository.DestinationRepository;
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
public class DestinationService {
    private final DestinationMapper destinationMapper;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);

    public DestinationService(
            DestinationMapper destinationMapper,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            UserQueryBuilder userQueryBuilder, DestinationQueryBuilder destinationQueryBuilder
    ) {
        this.destinationMapper = destinationMapper;
        this.destinationPersistence = destinationPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
    }

    public List<DestinationBasicPlusDto> getRandomNumOfDestinations(int limit) {
        long countOfDestinations = this.destinationQueryBuilder.getDestinationCount();
        // TODO: implement error logic if no destinations are available
        // TODO: return all destinations if count <= limit
        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfDestinations);

        return this.destinationQueryBuilder.getDestinationsByIds(randomIds);
    }

    public DestinationDetailsDto getDestinationDetailsById(Long destinationId) {
        DestinationEntity destinationById = this.destinationQueryBuilder.getDestinationById(destinationId);

        return this.destinationMapper.destinationEntityToDestinationDetailsDto(destinationById);
    }

    public Page<DestinationBasicPlusDto> getAllDestinations(Pageable pageable) {
        return this.destinationQueryBuilder.getAllDestinations(pageable);
    }

    public List<DestinationBasicDto> selectAll() {
        return this.destinationQueryBuilder.selectAllApprovedDestinations();
    }

    public DestinationIdDto createDestination(
            DestinationCreateDto destinationCreateDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        DestinationEntity newDestination =
                this.destinationMapper.destinationCreateDtoToDestinationEntity(destinationCreateDto);
        newDestination.setDestinationStatus(StatusEnum.PENDING);
        newDestination.setCreatedBy(validUser);

//        logger.debug("{}", newDestination);
        DestinationEntity saved = this.destinationPersistence.saveEntityWithReturn(newDestination);
        return new DestinationIdDto(saved.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getPendingApprovalDestinationCount() {
        return this.destinationQueryBuilder.getDestinationCountByStatus(StatusEnum.PENDING);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getUnderReviewDestinationCount() {
        return this.destinationQueryBuilder.getDestinationCountByStatus(StatusEnum.REVIEW);
    }
}
