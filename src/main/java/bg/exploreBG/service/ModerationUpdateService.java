package bg.exploreBG.service;

import bg.exploreBG.interfaces.composed.UpdatableEntity;
import bg.exploreBG.interfaces.UpdatableEntityDto;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateOrReviewDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.utils.EntityUpdateUtils;
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
import java.util.stream.Collectors;

@Service
public class ModerationUpdateService {
    private final AccommodationService accommodationService;
    private final DestinationService destinationService;
    private final GeometryFactory geometryFactory;
    private final Logger logger = LoggerFactory.getLogger(ModerationUpdateService.class);

    public ModerationUpdateService(
            AccommodationService accommodationService,
            DestinationService destinationService,
            GeometryFactory geometryFactory
    ) {
        this.accommodationService = accommodationService;
        this.destinationService = destinationService;
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
                EntityUpdateUtils.updateFieldIfDifferent(dest::getDestinationName, dest::setDestinationName, dto.destinationName()));

        isUpdated |= logUpdate("Location",
                updateLocation(dto.latitude(), dto.longitude(), dest));

        isUpdated |= logUpdate("DestinationInfo",
                EntityUpdateUtils.updateFieldIfDifferent(dest::getDestinationInfo, dest::setDestinationInfo, dto.destinationInfo()));

        isUpdated |= logUpdate("NextTo",
                EntityUpdateUtils.updateFieldIfDifferent(dest::getNextTo, dest::setNextTo, dto.nextTo()));

        isUpdated |= logUpdate("Type",
                EntityUpdateUtils.updateFieldIfDifferent(dest::getType, dest::setType, dto.type()));

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
                EntityUpdateUtils.updateFieldIfDifferent(accom::getAccommodationName, accom::setAccommodationName, dto.accommodationName()));

        isUpdated |= logUpdate("PhoneNumber",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getPhoneNumber, accom::setPhoneNumber, dto.phoneNumber()));

        isUpdated |= logUpdate("Site",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getSite, accom::setSite, dto.site()));

        isUpdated |= logUpdate("AccommodationInfo",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getAccommodationInfo, accom::setAccommodationInfo, dto.accommodationInfo()));

        isUpdated |= logUpdate("BedCapacity",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getBedCapacity, accom::setBedCapacity, dto.bedCapacity()));

        isUpdated |= logUpdate("PricePerBed",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getPricePerBed, accom::setPricePerBed, dto.pricePerBed()));

        isUpdated |= logUpdate("FoodAvailable",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getAvailableFood, accom::setAvailableFood, dto.availableFood()));

        isUpdated |= logUpdate("Access",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getAccess, accom::setAccess, dto.access()));

        isUpdated |= logUpdate("NextTo",
                EntityUpdateUtils.updateFieldIfDifferent(accom::getNextTo, accom::setNextTo, dto.nextTo()));

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
                EntityUpdateUtils.updateFieldIfDifferent(trail::getStartPoint, trail::setStartPoint, dto.startPoint()));

        isUpdated |= logUpdate("EndPoint",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getEndPoint, trail::setEndPoint, dto.endPoint()));

        isUpdated |= logUpdate("TotalDifference",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getTotalDistance, trail::setTotalDistance, dto.totalDistance()));

        isUpdated |= logUpdate("TrailInfo",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getTrailInfo, trail::setTrailInfo, dto.trailInfo()));

        isUpdated |= logUpdate("SeasonVisited",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getSeasonVisited, trail::setSeasonVisited, dto.seasonVisited()));

        isUpdated |= logUpdate("WaterAvailable",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getWaterAvailability, trail::setWaterAvailability, dto.waterAvailability()));

        isUpdated |= logUpdate("TrailDifficulty",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getTrailDifficulty, trail::setTrailDifficulty, dto.trailDifficulty()));

        isUpdated |= logUpdate("Activity",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getActivity, trail::setActivity, dto.activity()));

        isUpdated |= logUpdate("ElevationGained",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getElevationGained, trail::setElevationGained, dto.elevationGained()));

        isUpdated |= logUpdate("NextTo",
                EntityUpdateUtils.updateFieldIfDifferent(trail::getNextTo, trail::setNextTo, dto.nextTo()));

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
            List<AccommodationEntity> accommodationEntities =
                    this.accommodationService.mapDtoToAccommodationEntities(newHuts);
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
            List<DestinationEntity> destinationEntities =
                    this.destinationService.mapDtoToDestinationEntities(newDestinations);
            currentTrail.setDestinations(destinationEntities);
            return true;
        }
        return false;
    }

    private boolean logUpdate(String field, boolean changed) {
        if (changed) {
            logger.info("{} changed.", field);
        }
        return changed;
    }
}
