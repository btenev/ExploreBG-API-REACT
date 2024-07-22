package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.repository.HikingTrailRepository;
import bg.exploreBG.utils.RandomUtil;
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
import java.util.stream.Collectors;

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
                    = mapDtoToAccommodationEntities(hikingTrailCreateDto.availableHuts());
            newHikingTrail.setAvailableHuts(accommodationEntities);
        }

        return this.hikingTrailRepository.save(newHikingTrail).getId();
    }

    public HikingTrailStartPointDto updateHikingTrailStartPoint(
            Long id,
            HikingTrailUpdateStartPointDto hikingTrailStartPointDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !hikingTrailStartPointDto.startPoint().equals(currentTrail.getStartPoint());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setStartPoint(hikingTrailStartPointDto.startPoint());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailStartPointDto(saved.getStartPoint());
    }

    public HikingTrailEndPointDto updateHikingTrailEndPoint(
            Long id,
            HikingTrailUpdateEndPointDto hikingTrailEndPointDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !hikingTrailEndPointDto.endPoint().equals(currentTrail.getEndPoint());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setEndPoint(hikingTrailEndPointDto.endPoint());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailEndPointDto(saved.getEndPoint());
    }

    public HikingTrailTotalDistanceDto updateHikingTrailTotalDistance(
            Long id,
            HikingTrailUpdateTotalDistanceDto trailTotalDistanceDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !trailTotalDistanceDto.totalDistance().equals(currentTrail.getTotalDistance());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setTotalDistance(trailTotalDistanceDto.totalDistance());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailTotalDistanceDto(saved.getTotalDistance());
    }

    public HikingTrailElevationGainedDto updateHikingTrailElevationGained(
            Long id,
            HikingTrailUpdateElevationGainedDto elevationGainedDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !elevationGainedDto.elevationGained().equals(currentTrail.getElevationGained());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setElevationGained(elevationGainedDto.elevationGained());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailElevationGainedDto(saved.getElevationGained());
    }

    public HikingTrailWaterAvailableDto updateHikingTrailWaterAvailable(
            Long id,
            HikingTrailUpdateWaterAvailableDto hikingTrailWaterAvailableDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !hikingTrailWaterAvailableDto.waterAvailable().equals(currentTrail.getWaterAvailable());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setWaterAvailable(hikingTrailWaterAvailableDto.waterAvailable());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailWaterAvailableDto(saved.getWaterAvailable().getValue());
    }

    public HikingTrailActivityDto updateHikingTrailActivity(
            Long id,
            HikingTrailUpdateActivityDto hikingTrailActivityDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        List<SuitableForEnum> currentTrailActivity = currentTrail.getActivity();

        boolean noMatch = !hikingTrailActivityDto.activity().equals(currentTrailActivity);
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setActivity(hikingTrailActivityDto.activity());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailActivityDto(saved.getActivity().stream().map(SuitableForEnum::getValue).collect(Collectors.toList()));
    }

    public HikingTrailTrailInfoDto updateHikingTrailTrailInfo(
            Long id,
            HikingTrailUpdateTrailInfoDto trailInfoDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !trailInfoDto.trailInfo().equals(currentTrail.getTrailInfo());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setTrailInfo(trailInfoDto.trailInfo());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailTrailInfoDto(saved.getTrailInfo());
    }

    public List<AccommodationBasicDto> updateHikingTrailAvailableHuts(
            Long id,
            HikingTrailUpdateAvailableHutsDto hikingTrailAvailableHutsDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        List<AccommodationEntity> currentTrailAvailableHuts = currentTrail.getAvailableHuts();
        List<AccommodationIdDto> currentHutsDto =
                currentTrailAvailableHuts
                        .stream()
                        .map(ae -> new AccommodationIdDto(ae.getId()))
                        .toList();

        boolean noMatch = !currentHutsDto.equals(hikingTrailAvailableHutsDto.availableHuts());
        HikingTrailEntity saved;

        if (noMatch) {
            List<AccommodationEntity> newSelection =
                    mapDtoToAccommodationEntities(hikingTrailAvailableHutsDto.availableHuts());
            currentTrail.setAvailableHuts(newSelection);
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return saved
                .getAvailableHuts()
                .stream()
                .map(hut -> new AccommodationBasicDto(hut.getId(), hut.getAccommodationName()))
                .collect(Collectors.toList());
    }

    public List<DestinationBasicDto> updateHikingTrailDestinations(
            Long id,
            HikingTrailUpdateDestinationsDto hikingTrailDestinationsDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        List<DestinationEntity> currentTrailDestinations = currentTrail.getDestinations();
        List<DestinationIdDto> destinationIdDto =
                currentTrailDestinations
                        .stream()
                        .map(de -> new DestinationIdDto(de.getId()))
                        .toList();

        boolean noMatch = ! destinationIdDto.equals(hikingTrailDestinationsDto.destinations());
        HikingTrailEntity saved;

        if (noMatch) {
            List<DestinationEntity> newSelection =
                    mapDtoToDestinationEntities(hikingTrailDestinationsDto.destinations());
            currentTrail.setDestinations(newSelection);
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return saved
                .getDestinations()
                .stream()
                .map(destination -> new DestinationBasicDto(destination.getId(), destination.getDestinationName()))
                .collect(Collectors.toList());
    }

    public List<HikingTrailIdTrailNameDto> selectAll() {
        return this.hikingTrailRepository.findAllBy();
    }

    private HikingTrailEntity verifiedHikingTrail(Long id, UserDetails userDetails) {

        HikingTrailEntity currentTrail = hikingTrailExist(id);
        UserEntity createdBy = currentTrail.getCreatedBy();

        this.userService.verifiedUser(createdBy, userDetails); // throws exception if no match
        return currentTrail;
    }

    protected HikingTrailEntity hikingTrailExist(Long id) {
        Optional<HikingTrailEntity> trailById = this.hikingTrailRepository.findById(id);

        if (trailById.isEmpty()) {
            throw new AppException("Hiking trail not found!", HttpStatus.NOT_FOUND);
        }
        return trailById.get();
    }

    private List<AccommodationEntity> mapDtoToAccommodationEntities(List<AccommodationIdDto> ids) {

        List<Long> accommodationIds = ids.stream().map(AccommodationIdDto::id).toList();

        return this.accommodationService.getAccommodationsById(accommodationIds);
    }

    private List<DestinationEntity> mapDtoToDestinationEntities(List<DestinationIdDto> ids) {

        List<Long> destinationIds = ids.stream().map(DestinationIdDto::id).toList();

        return this.destinationService.getDestinationsByIds(destinationIds);
    }

}
