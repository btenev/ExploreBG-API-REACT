package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.updatable.UpdatableEntity;
import bg.exploreBG.updatable.UpdatableEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class EntityUpdateService {
    private final AccommodationService accommodationService;
    private final DestinationService destinationService;

    private final Logger logger = LoggerFactory.getLogger(EntityUpdateService.class);

    public EntityUpdateService(AccommodationService accommodationService, DestinationService destinationService) {
        this.accommodationService = accommodationService;
        this.destinationService = destinationService;
    }

    public <T extends UpdatableEntity> void updateFieldsIfNecessary(T entity, UpdatableEntityDto<T> dto) {

        if (entity instanceof HikingTrailEntity) {
            updateTrailFieldsIfDifferent((HikingTrailEntity) entity, (HikingTrailCreateOrReviewDto) dto);
        }
    }

    private void updateTrailFieldsIfDifferent(
            HikingTrailEntity trail,
            HikingTrailCreateOrReviewDto dto
    ) {
        boolean isUpdated = false;

        isUpdated |= updateFieldIfDifferent(trail::getStartPoint, trail::setStartPoint, dto.startPoint());
        logger.info("StartPoint: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getEndPoint, trail::setEndPoint, dto.endPoint());
        logger.info("EndPoint: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getTotalDistance, trail::setTotalDistance, dto.totalDistance());
        logger.info("TotalDifference: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getTrailInfo, trail::setTrailInfo, dto.trailInfo());
        logger.info("TrailInfo: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getSeasonVisited, trail::setSeasonVisited, dto.seasonVisited());
        logger.info("SeasonVisited: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getWaterAvailable, trail::setWaterAvailable, dto.waterAvailable());
        logger.info("WaterAvailable: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getTrailDifficulty, trail::setTrailDifficulty, dto.trailDifficulty());
        logger.info("TrailDifficulty: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getActivity, trail::setActivity, dto.activity());
        logger.info("Activity: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getElevationGained, trail::setElevationGained, dto.elevationGained());
        logger.info("ElevationGained: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(trail::getNextTo, trail::setNextTo, dto.nextTo());
        logger.info("NextTo: {}", isUpdated);
        isUpdated |= updateAccommodationList(trail, dto.availableHuts());
        logger.info("AvailableHuts: {}", isUpdated);
        isUpdated |= updateDestinationList(trail, dto.destinations());
        logger.info("Destinations: {}", isUpdated);


        if (isUpdated) {
            trail.setModificationDate(LocalDateTime.now());
        }
    }

    public boolean updateAccommodationList(HikingTrailEntity currentTrail, Set<AccommodationIdDto> newHuts) {
        Set<AccommodationIdDto> currentHuts = currentTrail.getAvailableHuts()
                .stream()
                .map(a -> new AccommodationIdDto(a.getId()))
                .collect(Collectors.toSet());

        if (!Objects.equals(currentHuts, newHuts)) {
            List<AccommodationEntity> accommodationEntities = mapDtoToAccommodationEntities(newHuts);
            currentTrail.setAvailableHuts(accommodationEntities);
            return true;
        }
        return false;
    }

    public boolean updateDestinationList(HikingTrailEntity currentTrail, Set<DestinationIdDto> newDestinations) {
        Set<DestinationIdDto> currentDestinations = currentTrail.getDestinations()
                .stream()
                .map(de -> new DestinationIdDto(de.getId()))
                .collect(Collectors.toSet());

        if (!Objects.equals(currentDestinations, newDestinations)) {
            List<DestinationEntity> destinationEntities = mapDtoToDestinationEntities(newDestinations);
            currentTrail.setDestinations(destinationEntities);
            return true;
        }
        return false;
    }

    public List<AccommodationEntity> mapDtoToAccommodationEntities(Set<AccommodationIdDto> ids) {

        List<Long> accommodationIds = ids.stream().map(AccommodationIdDto::id).toList();

        return this.accommodationService.getAccommodationsById(accommodationIds);
    }

    public List<DestinationEntity> mapDtoToDestinationEntities(Set<DestinationIdDto> ids) {

        List<Long> destinationIds = ids.stream().map(DestinationIdDto::id).toList();

        return this.destinationService.getDestinationsByIds(destinationIds);
    }

    public <T> boolean updateFieldIfDifferent(Supplier<T> getter, Consumer<T> setter, T newValue) {
        T currentValue = getter.get();

        if (!Objects.equals(currentValue, newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }
}
