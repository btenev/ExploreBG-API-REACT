package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.accommodation.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.DestinationIdDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailTotalDistance;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateTotalDistance;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        HikingTrailEntity trailById = hikingTrailExist(id);

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(trailById);
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

//        logger.debug("{}", newHikingTrail);

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

    public HikingTrailTotalDistance updateHikingTrailTotalDistance(
            Long id,
            HikingTrailUpdateTotalDistance hikingTrailUpdateTotalDistance,
            UserDetails userDetails
    ) {
        UserEntity currentUser = this.userService.userExist(userDetails.getUsername());

        HikingTrailEntity currentTrail = this.hikingTrailExist(id);
        UserEntity createdBy = currentTrail.getCreatedBy();

        // TODO: https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.3 FORBIDDEN vs NOT FOUND
        if (!currentUser.equals(createdBy)) {
            throw new AppException("No access to this resource!", HttpStatus.FORBIDDEN);
        }

        currentTrail.setTotalDistance(hikingTrailUpdateTotalDistance.totalDistance());
        HikingTrailEntity saved = this.hikingTrailRepository.save(currentTrail);
        return new HikingTrailTotalDistance(saved.getTotalDistance());
    }

    private HikingTrailEntity hikingTrailExist(Long id) {
        Optional<HikingTrailEntity> trailById = this.hikingTrailRepository.findById(id);

        if (trailById.isEmpty()) {
            throw new AppException("Hiking trail not found!", HttpStatus.NOT_FOUND);
        }
        return trailById.get();
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
