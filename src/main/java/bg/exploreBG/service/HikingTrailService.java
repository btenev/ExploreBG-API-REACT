package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailElevationGainedDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailTotalDistanceDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailTrailInfoDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateElevationGainedDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateTotalDistanceDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateTrailInfoDto;
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

    private static final Logger logger = LoggerFactory.getLogger(HikingTrailService.class);
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
        UserEntity validUser = this.userService.verifiedUser(id, userDetails);

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

    public HikingTrailTrailInfoDto updateHikingTrailTrailInfo(
            Long id,
            HikingTrailUpdateTrailInfoDto hikingTrailUpdateTrailInfoDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        currentTrail.setTrailInfo(hikingTrailUpdateTrailInfoDto.trailInfo());

        HikingTrailEntity saved = this.hikingTrailRepository.save(currentTrail);
        return new HikingTrailTrailInfoDto(saved.getTrailInfo());
    }

    public HikingTrailTotalDistanceDto updateHikingTrailTotalDistance(
            Long id,
            HikingTrailUpdateTotalDistanceDto hikingTrailUpdateTotalDistanceDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        currentTrail.setTotalDistance(hikingTrailUpdateTotalDistanceDto.totalDistance());

        HikingTrailEntity saved = this.hikingTrailRepository.save(currentTrail);
        return new HikingTrailTotalDistanceDto(saved.getTotalDistance());
    }

    public HikingTrailElevationGainedDto updateHikingTrailElevationGained(
            Long id,
            HikingTrailUpdateElevationGainedDto hikingTrailUpdateElevationGainedDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        currentTrail.setElevationGained(hikingTrailUpdateElevationGainedDto.elevationGained());

        HikingTrailEntity saved = this.hikingTrailRepository.save(currentTrail);
        return new HikingTrailElevationGainedDto(saved.getElevationGained());
    }

    private HikingTrailEntity verifiedHikingTrail(Long id, UserDetails userDetails) {

        HikingTrailEntity currentTrail = hikingTrailExist(id);
        UserEntity createdBy = currentTrail.getCreatedBy();

        this.userService.verifiedUser(createdBy, userDetails); // throws exception if no match
        return currentTrail;
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
