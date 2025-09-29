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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
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
    private final GeometryFactory geometryFactory;
    private final Logger logger = LoggerFactory.getLogger(EntityUpdateService.class);

    public EntityUpdateService(
            AccommodationQueryBuilder accommodationQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            GeometryFactory geometryFactory
    ) {
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.geometryFactory = geometryFactory;
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
            DestinationEntity dest,
            DestinationCreateOrReviewDto dto
    ) {
        boolean isUpdated = false;

        isUpdated |= logUpdate("DestinationName",
                updateFieldIfDifferent(dest::getDestinationName, dest::setDestinationName, dto.destinationName()));

        isUpdated |= logUpdate("Location",
                updateLocation(dto.latitude(), dto.longitude(), dest));

        isUpdated |= logUpdate("DestinationInfo", updateFieldIfDifferent(
                dest::getDestinationInfo, dest::setDestinationInfo, dto.destinationInfo()));

        isUpdated |= logUpdate("NextTo",
                updateFieldIfDifferent(dest::getNextTo, dest::setNextTo, dto.nextTo()));

        isUpdated |= logUpdate("Type",
                updateFieldIfDifferent(dest::getType, dest::setType, dto.type()));

        if (isUpdated) {
            dest.setModificationDate(LocalDateTime.now());
        }
    }

    private void updateAccommodationFieldsIfDifferent(
            AccommodationEntity accom,
            AccommodationCreateOrReviewDto dto
    ) {
        boolean isUpdated = false;

        isUpdated |= logUpdate("AccommodationName",
                updateFieldIfDifferent(accom::getAccommodationName, accom::setAccommodationName, dto.accommodationName()));

        isUpdated |= logUpdate("PhoneNumber",
                updateFieldIfDifferent(accom::getPhoneNumber, accom::setPhoneNumber, dto.phoneNumber()));

        isUpdated |= logUpdate("Site",
                updateFieldIfDifferent(accom::getSite, accom::setSite, dto.site()));

        isUpdated |= logUpdate("AccommodationInfo",
                updateFieldIfDifferent(accom::getAccommodationInfo, accom::setAccommodationInfo, dto.accommodationInfo()));

        isUpdated |= logUpdate("BedCapacity",
                updateFieldIfDifferent(accom::getBedCapacity, accom::setBedCapacity, dto.bedCapacity()));

        isUpdated |= logUpdate("PricePerBed",
                updateFieldIfDifferent(accom::getPricePerBed, accom::setPricePerBed, dto.pricePerBed()));

        isUpdated |= logUpdate("FoodAvailable",
                updateFieldIfDifferent(accom::getAvailableFood, accom::setAvailableFood, dto.availableFood()));

        isUpdated |= logUpdate("Access",
                updateFieldIfDifferent(accom::getAccess, accom::setAccess, dto.access()));

        isUpdated |= logUpdate("NextTo",
                updateFieldIfDifferent(accom::getNextTo, accom::setNextTo, dto.nextTo()));

        if (isUpdated) {
            accom.setModificationDate(LocalDateTime.now());
        }
    }

    private void updateTrailFieldsIfDifferent(
            HikingTrailEntity trail,
            HikingTrailCreateOrReviewDto dto
    ) {
        boolean isUpdated = false;

        isUpdated |= logUpdate("StartPoint",
                updateFieldIfDifferent(trail::getStartPoint, trail::setStartPoint, dto.startPoint()));

        isUpdated |= logUpdate("EndPoint",
                updateFieldIfDifferent(trail::getEndPoint, trail::setEndPoint, dto.endPoint()));

        isUpdated |= logUpdate("TotalDifference",
                updateFieldIfDifferent(trail::getTotalDistance, trail::setTotalDistance, dto.totalDistance()));

        isUpdated |= logUpdate("TrailInfo",
                updateFieldIfDifferent(trail::getTrailInfo, trail::setTrailInfo, dto.trailInfo()));

        isUpdated |= logUpdate("SeasonVisited",
                updateFieldIfDifferent(trail::getSeasonVisited, trail::setSeasonVisited, dto.seasonVisited()));

        isUpdated |= logUpdate("WaterAvailable",
                updateFieldIfDifferent(trail::getWaterAvailability, trail::setWaterAvailability, dto.waterAvailability()));

        isUpdated |= logUpdate("TrailDifficulty",
                updateFieldIfDifferent(trail::getTrailDifficulty, trail::setTrailDifficulty, dto.trailDifficulty()));

        isUpdated |= logUpdate("Activity",
                updateFieldIfDifferent(trail::getActivity, trail::setActivity, dto.activity()));

        isUpdated |= logUpdate("ElevationGained",
                updateFieldIfDifferent(trail::getElevationGained, trail::setElevationGained, dto.elevationGained()));

        isUpdated |= logUpdate("NextTo",
                updateFieldIfDifferent(trail::getNextTo, trail::setNextTo, dto.nextTo()));

        isUpdated |= logUpdate("AvailableHuts",
                updateAccommodationList(trail, dto.availableHuts()));

        isUpdated |= logUpdate("Destinations",
                updateDestinationList(trail, dto.destinations()));

        if (isUpdated) {
            trail.setModificationDate(LocalDateTime.now());
        }
    }

    private boolean updateLocation(Double latitude, Double longitude, DestinationEntity destination) {
        if (latitude == null || longitude == null) return false;

        Point newPoint = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        Point current = destination.getLocation();

        final double TOLERANCE = 1e-6; // ~0.11 m

        if (current == null) {
            destination.setLocation(newPoint);
            return true;
        }

        if (!current.equalsExact(newPoint, TOLERANCE)) {
            destination.setLocation(newPoint);
            return true;
        }

        return false;
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

    private boolean logUpdate(String field, boolean changed) {
        if (changed) {
            logger.info("{} changed.", field);
        }
        return changed;
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
