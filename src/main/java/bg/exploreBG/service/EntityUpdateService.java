package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateOrReviewDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.updatable.UpdatableEntity;
import bg.exploreBG.updatable.UpdatableEntityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class EntityUpdateService {
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final Logger logger = LoggerFactory.getLogger(EntityUpdateService.class);

    public EntityUpdateService(
            AccommodationQueryBuilder accommodationQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder
    ) {
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
    }

    public <T extends UpdatableEntity> void updateFieldsIfNecessary(T entity, UpdatableEntityDto<T> dto) {

        if (entity instanceof HikingTrailEntity) {
            updateTrailFieldsIfDifferent((HikingTrailEntity) entity, (HikingTrailCreateOrReviewDto) dto);
        } else if (entity instanceof AccommodationEntity) {
            updateAccommodationFieldsIfDifferent((AccommodationEntity) entity, (AccommodationCreateOrReviewDto) dto);
        } else if (entity instanceof DestinationEntity) {
            updateDestinationFieldsIfDifferent((DestinationEntity) entity, (DestinationCreateOrReviewDto) dto);
        }
    }


    private void updateDestinationFieldsIfDifferent(
            DestinationEntity destination,
            DestinationCreateOrReviewDto dto
    ) {
        boolean isUpdated = false;

        isUpdated |= updateFieldIfDifferent(
                destination::getDestinationName, destination::setDestinationName, dto.destinationName());
        logger.info("DestinationName: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(destination::getLocation, destination::setLocation, dto.location());
        logger.info("Location: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(
                destination::getDestinationInfo, destination::setDestinationInfo, dto.destinationInfo());
        logger.info("DestinationInfo: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(destination::getNextTo, destination::setNextTo, dto.nextTo());
        logger.info("NextTo: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(destination::getType, destination::setType, dto.type());
        logger.info("Type: {}", isUpdated);

        if (isUpdated) {
            destination.setModificationDate(LocalDateTime.now());
        }
    }

    private void updateAccommodationFieldsIfDifferent(
            AccommodationEntity accommodation,
            AccommodationCreateOrReviewDto dto
    ) {
        boolean isUpdated = false;

        isUpdated |= updateFieldIfDifferent(accommodation::getAccommodationName, accommodation::setAccommodationName, dto.accommodationName());
        logger.info("AccommodationName: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getPhoneNumber, accommodation::setPhoneNumber, dto.phoneNumber());
        logger.info("PhoneNumber: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getSite, accommodation::setSite, dto.site());
        logger.info("Site: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getAccommodationInfo, accommodation::setAccommodationInfo, dto.accommodationInfo());
        logger.info("AccommodationInfo: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getBedCapacity, accommodation::setBedCapacity, dto.bedCapacity());
        logger.info("BedCapacity: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getPricePerBed, accommodation::setPricePerBed, dto.pricePerBed());
        logger.info("PricePerBed: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getAvailableFood, accommodation::setAvailableFood, dto.availableFood());
        logger.info("FoodAvailable: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getAccess, accommodation::setAccess, dto.access());
        logger.info("Access: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getAccess, accommodation::setAccess, dto.access());
        logger.info("Access: {}", isUpdated);
        isUpdated |= updateFieldIfDifferent(accommodation::getNextTo, accommodation::setNextTo, dto.nextTo());
        logger.info("NextTo: {}", isUpdated);

        if (isUpdated) {
            accommodation.setModificationDate(LocalDateTime.now());
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
        isUpdated |= updateFieldIfDifferent(trail::getWaterAvailability, trail::setWaterAvailability, dto.waterAvailability());
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

        return this.accommodationQueryBuilder
                .getAccommodationEntitiesByIdAndStatus(accommodationIds, StatusEnum.APPROVED);
    }

    public List<DestinationEntity> mapDtoToDestinationEntities(Set<DestinationIdDto> ids) {

        List<Long> destinationIds = ids.stream().map(DestinationIdDto::id).toList();

        return this.destinationQueryBuilder.getDestinationEntitiesByIdsAnStatus(destinationIds, StatusEnum.APPROVED);
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
