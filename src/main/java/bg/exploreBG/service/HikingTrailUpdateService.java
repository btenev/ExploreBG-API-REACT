package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.AccommodationWrapperDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.DestinationWrapperDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.utils.EntityUpdateUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HikingTrailUpdateService {
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final MainImageUpdater mainImageUpdater;
    private final DestinationService destinationService;
    private final AccommodationService accommodationService;
    private final EntityFieldUpdater entityFieldUpdater;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;

    public HikingTrailUpdateService(
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            MainImageUpdater mainImageUpdater,
            DestinationService destinationService,
            AccommodationService accommodationService,
            EntityFieldUpdater entityFieldUpdater,
            GenericPersistenceService<HikingTrailEntity> trailPersistence
    ) {
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.mainImageUpdater = mainImageUpdater;
        this.destinationService = destinationService;
        this.accommodationService = accommodationService;
        this.entityFieldUpdater = entityFieldUpdater;
        this.trailPersistence = trailPersistence;
    }

    public HikingTrailStartPointDto updateHikingTrailStartPoint(
            Long trailId,
            HikingTrailUpdateStartPointDto dto,
            UserDetails user
    ) {
        return updateTrailField(
                trailId,
                user,
                dto.startPoint(),
                HikingTrailEntity::getStartPoint,
                HikingTrailEntity::setStartPoint,
                (trail, isUpdated) -> new HikingTrailStartPointDto(
                        trail.getStartPoint(),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailEndPointDto updateHikingTrailEndPoint(
            Long trailId,
            HikingTrailUpdateEndPointDto dto,
            UserDetails userDetails
    ) {
        return updateTrailField(
                trailId,
                userDetails,
                dto.endPoint(),
                HikingTrailEntity::getEndPoint,
                HikingTrailEntity::setEndPoint,
                (trail, isUpdated) -> new HikingTrailEndPointDto(
                        trail.getEndPoint(),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailTotalDistanceDto updateHikingTrailTotalDistance(
            Long trailId,
            HikingTrailUpdateTotalDistanceDto dto,
            UserDetails userDetails
    ) {
        return updateTrailField(
                trailId,
                userDetails,
                dto.totalDistance(),
                HikingTrailEntity::getTotalDistance,
                HikingTrailEntity::setTotalDistance,
                (trail, isUpdated) -> new HikingTrailTotalDistanceDto(
                        trail.getTotalDistance(),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailElevationGainedDto updateHikingTrailElevationGained(
            Long trailId,
            HikingTrailUpdateElevationGainedDto dto,
            UserDetails userDetails
    ) {
        return updateTrailField(
                trailId,
                userDetails,
                dto.elevationGained(),
                HikingTrailEntity::getElevationGained,
                HikingTrailEntity::setElevationGained,
                (trail, isUpdated) -> new HikingTrailElevationGainedDto(
                        trail.getElevationGained(),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailWaterAvailabilityDto updateHikingTrailWaterAvailable(
            Long trailId,
            HikingTrailUpdateWaterAvailabilityDto dto,
            UserDetails userDetails
    ) {
        return updateTrailField(
                trailId,
                userDetails,
                dto.waterAvailability(),
                HikingTrailEntity::getWaterAvailability,
                HikingTrailEntity::setWaterAvailability,
                (trail, isUpdated) -> new HikingTrailWaterAvailabilityDto(
                        trail.getWaterAvailability().getValue(),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailActivityDto updateHikingTrailActivity(
            Long trailId,
            HikingTrailUpdateActivityDto dto,
            UserDetails userDetails
    ) {
        /*TODO: Test Object.equals with list, might need to change to set*/
        return updateTrailField(
                trailId,
                userDetails,
                dto.activity(),
                HikingTrailEntity::getActivity,
                HikingTrailEntity::setActivity,
                (trail, isUpdated) -> new HikingTrailActivityDto(
                        trail.getActivity().stream().map(SuitableForEnum::getValue).collect(Collectors.toList()),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailTrailInfoDto updateHikingTrailTrailInfo(
            Long trailId,
            HikingTrailUpdateTrailInfoDto dto,
            UserDetails userDetails
    ) {
        return updateTrailField(
                trailId,
                userDetails,
                dto.trailInfo(),
                HikingTrailEntity::getTrailInfo,
                HikingTrailEntity::setTrailInfo,
                (trail, isUpdated) -> new HikingTrailTrailInfoDto(
                        trail.getTrailInfo(),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public AccommodationWrapperDto updateHikingTrailAvailableHuts(
            Long trailId,
            HikingTrailUpdateAvailableHutsDto dto,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity trail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithHutsByIdAndStatusIfOwner(trailId, statuses, userDetails.getUsername());

        return this.entityFieldUpdater.updateEntityCollection(
                trail,
                trail::getAvailableHuts,
                trail::setAvailableHuts,
                dto.availableHuts(),
                this.accommodationService::mapDtoToAccommodationEntities,
                this.trailPersistence::saveEntityWithReturn,
                this::mapHutsToWrapper);
    }

    public DestinationWrapperDto updateHikingTrailDestinations(
            Long trailId,
            HikingTrailUpdateDestinationsDto dto,
            UserDetails user,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity trail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithDestinationsByAndStatusIfOwner(trailId, statuses, user.getUsername());

        return this.entityFieldUpdater.updateEntityCollection(
                trail,
                trail::getDestinations,
                trail::setDestinations,
                dto.destinations(),
                this.destinationService::mapDtoToDestinationEntities,
                this.trailPersistence::saveEntityWithReturn,
                this::mapHutsToDestination);
    }

    public HikingTrailDifficultyDto updateHikingTrailDifficulty(
            Long trailId,
            HikingTrailUpdateTrailDifficultyDto dto,
            UserDetails userDetails
    ) {
        return updateTrailField(
                trailId,
                userDetails,
                dto.trailDifficulty(),
                HikingTrailEntity::getTrailDifficulty,
                HikingTrailEntity::setTrailDifficulty,
                (trail, isUpdated) -> new HikingTrailDifficultyDto(
                        trail.getTrailDifficulty().getLevel(),
                        EntityUpdateUtils.getModificationDateIfUpdated(trail, isUpdated)));
    }

    public long updateHikingTrailMainImage(
            Long trailId,
            ImageMainUpdateDto dto,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity trail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithImagesByIdAndStatusIfOwner(trailId, statuses, userDetails.getUsername());

     return this.mainImageUpdater.updateMainImage(
             trail,
             dto,
             this.trailPersistence::saveEntityWithReturn);
    }

    private <T, R> R updateTrailField(
            Long trailId,
            UserDetails user,
            T newValue,
            Function<HikingTrailEntity, T> getter,
            BiConsumer<HikingTrailEntity, T> setter,
            BiFunction<HikingTrailEntity, Boolean, R> dtoMapper
    ) {
        HikingTrailEntity trail =
                hikingTrailQueryBuilder.
                        getHikingTrailByIdAndStatusIfOwner(trailId, user.getUsername());

        return this.entityFieldUpdater.updateEntityField(
                trail,
                () -> getter.apply(trail),
                val -> setter.accept(trail, val),
                newValue,
                this.trailPersistence::saveEntityWithReturn,
                dtoMapper);
    }

    private AccommodationWrapperDto mapHutsToWrapper(HikingTrailEntity trail, boolean updated) {
        List<AccommodationIdAndAccommodationName> availableHuts = trail.getAvailableHuts()
                .stream()
                .map(hut -> new AccommodationIdAndAccommodationName(hut.getId(), hut.getAccommodationName()))
                .collect(Collectors.toList());

        return new AccommodationWrapperDto(availableHuts, EntityUpdateUtils.getModificationDateIfUpdated(trail, updated));
    }

    private DestinationWrapperDto mapHutsToDestination(HikingTrailEntity trail, boolean updated) {
        List<DestinationIdAndDestinationNameDto> destinations = trail
                .getDestinations()
                .stream()
                .map(destination -> new DestinationIdAndDestinationNameDto(
                        destination.getId(), destination.getDestinationName()))
                .collect(Collectors.toList());

        return new DestinationWrapperDto(destinations, EntityUpdateUtils.getModificationDateIfUpdated(trail, updated));
    }
}
