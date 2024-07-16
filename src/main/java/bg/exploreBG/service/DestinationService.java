package bg.exploreBG.service;

import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.mapper.DestinationMapper;
import bg.exploreBG.repository.DestinationRepository;
import bg.exploreBG.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final DestinationMapper destinationMapper;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(DestinationService.class);
    public DestinationService(
            DestinationRepository destinationRepository,
            DestinationMapper destinationMapper,
            UserService userService
    ) {
        this.destinationRepository = destinationRepository;
        this.destinationMapper = destinationMapper;
        this.userService = userService;
    }

    public List<DestinationBasicPlusDto> getRandomNumOfDestinations(int limit) {
        long countOfDestinations = this.destinationRepository.count();
        // TODO: implement error logic if no destinations are available
        // TODO: return all destinations if count <= limit
        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfDestinations);

        return this.destinationRepository
                .findByIdIn(randomIds);
    }

    public DestinationDetailsDto getDestination(Long id) {
        Optional<DestinationEntity> byId = this.destinationRepository.findById(id);
        if (byId.isEmpty()) {
            // TODO: return error message
        }

        return this.destinationMapper.destinationEntityToDestinationDetailsDto(byId.get());
    }

    public Page<DestinationBasicPlusDto> getAllDestinations(Pageable pageable) {
        return this.destinationRepository
                .findAllBy(pageable);
    }

    public List<DestinationBasicDto> selectAll() {
        return this.destinationRepository
                .findAllByDestinationStatus(StatusEnum.APPROVED);
    }

    public List<DestinationEntity> getDestinationsByIds(List<Long> ids) {
        List<DestinationEntity> selected = new ArrayList<>();
        for (Long id : ids) {
            Optional<DestinationEntity> byId = this.destinationRepository.findById(id);
            byId.ifPresent(selected::add);
        }
        return selected;
    }

    public Long createDestination(
            Long id,
            DestinationCreateDto destinationCreateDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userService.verifiedUser(id, userDetails);
        DestinationEntity newDestination =
                this.destinationMapper.destinationCreateDtoToDestinationEntity(destinationCreateDto);
        newDestination.setDestinationStatus(StatusEnum.PENDING);

        logger.debug("{}", newDestination);

        return this.destinationRepository.save(newDestination).getId();
    }
}
