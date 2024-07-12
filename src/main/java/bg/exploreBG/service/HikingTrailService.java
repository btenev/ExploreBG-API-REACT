package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.DestinationIdDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.utils.RandomUtil;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.repository.HikingTrailRepository;
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
import java.util.stream.Collectors;

@Service
public class HikingTrailService {

    private static Logger logger = LoggerFactory.getLogger(HikingTrailService.class);
    private final HikingTrailRepository hikingTrailRepository;
    private final HikingTrailMapper hikingTrailMapper;
    private final UserService userService;
    private final DestinationService destinationService;
    private final AccommodationService accommodationService;

    public HikingTrailService(
            HikingTrailRepository hikingTrailRepository,
            HikingTrailMapper hikingTrailMapper,
            UserService userService,
            DestinationService destinationService,
            AccommodationService accommodationService
    ) {
        this.hikingTrailRepository = hikingTrailRepository;
        this.hikingTrailMapper = hikingTrailMapper;
        this.userService = userService;
        this.destinationService = destinationService;
        this.accommodationService = accommodationService;
    }

    public List<HikingTrailBasicDto> getRandomNumOfHikingTrails(int limit) {
        long countOfAvailableHikingTrails = this.hikingTrailRepository.count();
        // TODO: implement error logic if no hikingTrails are available

        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfAvailableHikingTrails);

        return this.hikingTrailRepository
                .findByIdIn(randomIds);
    }

    public HikingTrailDetailsDto getHikingTrail(Long id) {
        Optional<HikingTrailEntity> trailById = this.hikingTrailRepository.findById(id);

        if (trailById.isEmpty()) {
            //TODO: implement error logic
        }

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(trailById.get());
    }

    public Page<HikingTrailBasicDto> getAllHikingTrails(Pageable pageable) {
        return this.hikingTrailRepository
                .findAll(pageable)
                .map(this.hikingTrailMapper::hikingTrailEntityToHikingTrailBasicDto);
    }

    public Long createHikingTrail(
            Long id,
            HikingTrailCreateDto hikingTrailCreateDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userService.validUser(id, userDetails);

        HikingTrailEntity newHikingTrail =
                this.hikingTrailMapper
                        .hikingTrailCreateDtoToHikingTrailEntity(hikingTrailCreateDto);

        logger.debug("{}", newHikingTrail);

        newHikingTrail.setTrailStatus(StatusEnum.PENDING);
        newHikingTrail.setCreatedBy(validUser);

        if (!hikingTrailCreateDto.destinations().isEmpty()) {
            List<DestinationEntity> destinationEntities =
                    mapDtoToDestinationEntities(hikingTrailCreateDto.destinations());
            newHikingTrail.setDestinations(destinationEntities);
        }

        if (!hikingTrailCreateDto.availableHuts().isEmpty()) {
            List<AccommodationEntity> accommodationEntities
                    = mapDtoToAvailableHuts(hikingTrailCreateDto.availableHuts());
            newHikingTrail.setAvailableHuts(accommodationEntities);
        }

        return this.hikingTrailRepository.save(newHikingTrail).getId();
    }

    private List<AccommodationEntity> mapDtoToAvailableHuts(List<AccommodationIdDto> ids) {

        List<Long> accommodationIds = ids.stream().map(AccommodationIdDto::id).toList();

        return this.accommodationService.getAccommodationsById(accommodationIds);
    }

    private List<DestinationEntity> mapDtoToDestinationEntities(List<DestinationIdDto> ids) {

        List<Long> destinationIds = ids.stream().map(DestinationIdDto::id).toList();

        return this.destinationService.getDestinationsByIds(destinationIds);
    }

}
