package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.dto.accommodation.AccommodationWrapperDto;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.dto.destination.DestinationWrapperDto;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.utils.ImageUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HikingTrailUpdateService {
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final EntityUpdateService entityUpdateService;
    private final GenericPersistenceService<HikingTrailEntity> trailPersistence;

    public HikingTrailUpdateService(
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            EntityUpdateService entityUpdateService,
            GenericPersistenceService<HikingTrailEntity> trailPersistence
    ) {
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.entityUpdateService = entityUpdateService;
        this.trailPersistence = trailPersistence;
    }

    public HikingTrailStartPointDto updateHikingTrailStartPoint(
            Long trailId,
            HikingTrailUpdateStartPointDto dto,
            UserDetails userDetails
    ) {
        return updateSimpleField(
                trailId,
                userDetails,
                dto.startPoint(),
                HikingTrailEntity::getStartPoint,
                HikingTrailEntity::setStartPoint,
                (trail, isUpdated) -> new HikingTrailStartPointDto(
                        trail.getStartPoint(),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailEndPointDto updateHikingTrailEndPoint(
            Long trailId,
            HikingTrailUpdateEndPointDto dto,
            UserDetails userDetails
    ) {
        return updateSimpleField(
                trailId,
                userDetails,
                dto.endPoint(),
                HikingTrailEntity::getEndPoint,
                HikingTrailEntity::setEndPoint,
                (trail, isUpdated) -> new HikingTrailEndPointDto(
                        trail.getEndPoint(),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailTotalDistanceDto updateHikingTrailTotalDistance(
            Long trailId,
            HikingTrailUpdateTotalDistanceDto dto,
            UserDetails userDetails
    ) {
        return updateSimpleField(
                trailId,
                userDetails,
                dto.totalDistance(),
                HikingTrailEntity::getTotalDistance,
                HikingTrailEntity::setTotalDistance,
                (trail, isUpdated) -> new HikingTrailTotalDistanceDto(
                        trail.getTotalDistance(),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailElevationGainedDto updateHikingTrailElevationGained(
            Long trailId,
            HikingTrailUpdateElevationGainedDto dto,
            UserDetails userDetails
    ) {
        return updateSimpleField(
                trailId,
                userDetails,
                dto.elevationGained(),
                HikingTrailEntity::getElevationGained,
                HikingTrailEntity::setElevationGained,
                (trail, isUpdated) -> new HikingTrailElevationGainedDto(
                        trail.getElevationGained(),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailWaterAvailabilityDto updateHikingTrailWaterAvailable(
            Long trailId,
            HikingTrailUpdateWaterAvailabilityDto dto,
            UserDetails userDetails
    ) {
        return updateSimpleField(
                trailId,
                userDetails,
                dto.waterAvailability(),
                HikingTrailEntity::getWaterAvailability,
                HikingTrailEntity::setWaterAvailability,
                (trail, isUpdated) -> new HikingTrailWaterAvailabilityDto(
                        trail.getWaterAvailability().getValue(),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailActivityDto updateHikingTrailActivity(
            Long trailId,
            HikingTrailUpdateActivityDto dto,
            UserDetails userDetails
    ) {
        /*TODO: Test Object.equals with list, might need to change to set*/
        return updateSimpleField(
                trailId,
                userDetails,
                dto.activity(),
                HikingTrailEntity::getActivity,
                HikingTrailEntity::setActivity,
                (trail, isUpdated) -> new HikingTrailActivityDto(
                        trail.getActivity().stream().map(SuitableForEnum::getValue).collect(Collectors.toList()),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public HikingTrailTrailInfoDto updateHikingTrailTrailInfo(
            Long trailId,
            HikingTrailUpdateTrailInfoDto dto,
            UserDetails userDetails
    ) {
        return updateSimpleField(
                trailId,
                userDetails,
                dto.trailInfo(),
                HikingTrailEntity::getTrailInfo,
                HikingTrailEntity::setTrailInfo,
                (trail, isUpdated) -> new HikingTrailTrailInfoDto(
                        trail.getTrailInfo(),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public AccommodationWrapperDto updateHikingTrailAvailableHuts(
            Long trailId,
            HikingTrailUpdateAvailableHutsDto dto,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithHutsByIdAndStatusIfOwner(trailId, statuses, userDetails.getUsername());
        /*TODO: Test Object.equals with list, might need to change to set*/
        boolean isUpdated = this.entityUpdateService.updateAccommodationList(currentTrail, dto.availableHuts());

        currentTrail = updateTrailStatusAndSaveIfChanged(currentTrail, isUpdated);

        List<AccommodationIdAndAccommodationName> availableHuts = currentTrail
                .getAvailableHuts()
                .stream()
                .map(hut -> new AccommodationIdAndAccommodationName(hut.getId(), hut.getAccommodationName()))
                .collect(Collectors.toList());

        return new AccommodationWrapperDto(
                availableHuts,
                getModificationDateIfUpdated(currentTrail, isUpdated));
    }

    public DestinationWrapperDto updateHikingTrailDestinations(
            Long trailId,
            HikingTrailUpdateDestinationsDto newDestinations,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithDestinationsByAndStatusIfOwner(trailId, statuses, userDetails.getUsername());
        /*TODO: Test Object.equals with list, might need to change to set*/
        boolean isUpdated = this.entityUpdateService.updateDestinationList(currentTrail, newDestinations.destinations());

        currentTrail = updateTrailStatusAndSaveIfChanged(currentTrail, isUpdated);

        List<DestinationIdAndDestinationNameDto> destinations = currentTrail
                .getDestinations()
                .stream()
                .map(destination -> new DestinationIdAndDestinationNameDto(destination.getId(), destination.getDestinationName()))
                .collect(Collectors.toList());

        return new DestinationWrapperDto(
                destinations,
                getModificationDateIfUpdated(currentTrail, isUpdated)
        );
    }

    public HikingTrailDifficultyDto updateHikingTrailDifficulty(
            Long trailId,
            HikingTrailUpdateTrailDifficultyDto dto,
            UserDetails userDetails
    ) {
        return updateSimpleField(
                trailId,
                userDetails,
                dto.trailDifficulty(),
                HikingTrailEntity::getTrailDifficulty,
                HikingTrailEntity::setTrailDifficulty,
                (trail, isUpdated) -> new HikingTrailDifficultyDto(
                        trail.getTrailDifficulty().getLevel(),
                        getModificationDateIfUpdated(trail, isUpdated)));
    }

    public boolean updateHikingTrailMainImage(
            Long trailId,
            ImageMainUpdateDto dto,
            UserDetails userDetails,
            List<StatusEnum> statuses
    ) {
        HikingTrailEntity currentTrail =
                this.hikingTrailQueryBuilder
                        .getHikingTrailWithImagesByIdAndStatusIfOwner(trailId, statuses, userDetails.getUsername());

        ImageEntity found = ImageUtils.filterMainImage(currentTrail.getImages(), dto.imageId());

        boolean isUpdated =
                this.entityUpdateService
                        .updateFieldIfDifferent(currentTrail::getMainImage, currentTrail::setMainImage, found);

        if (isUpdated) {
            currentTrail.setMainImage(found);
            this.trailPersistence.saveEntityWithoutReturn(currentTrail);
        }

        return true;
    }

    private <T, R> R updateSimpleField(
            Long trailId,
            UserDetails userDetails,
            T newValue,
            Function<HikingTrailEntity, T> getter,
            BiConsumer<HikingTrailEntity, T> setter,
            BiFunction<HikingTrailEntity, Boolean, R> dtoMapper
    ) {
        HikingTrailEntity trail =
                hikingTrailQueryBuilder.
                        getHikingTrailByIdAndStatusIfOwner(trailId, userDetails.getUsername());
        return updateHikingTrailField(trail, getter, setter, newValue, dtoMapper);
    }

    private <T, R> R updateHikingTrailField(
            HikingTrailEntity trail,
            Function<HikingTrailEntity, T> getter,
            BiConsumer<HikingTrailEntity, T> setter,
            T newValue,
            BiFunction<HikingTrailEntity, Boolean, R> dtoMapper
    ) {
        final HikingTrailEntity finalTrail = trail;

        boolean isUpdated = this.entityUpdateService.updateFieldIfDifferent(
                () -> getter.apply(finalTrail),
                val -> setter.accept(finalTrail, val),
                newValue);

        trail = updateTrailStatusAndSaveIfChanged(trail, isUpdated);
        return dtoMapper.apply(trail, isUpdated);
    }

    private HikingTrailEntity updateTrailStatusAndSaveIfChanged(
            HikingTrailEntity trail,
            boolean isUpdated
    ) {
        if (isUpdated) {
            trail.setStatus(StatusEnum.PENDING);
            trail.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
            trail.setModificationDate(LocalDateTime.now());
            trail = this.trailPersistence.saveEntityWithReturn(trail);
        }
        return trail;
    }

    private LocalDateTime getModificationDateIfUpdated(HikingTrailEntity trail, boolean isUpdated) {
        return isUpdated ? trail.getModificationDate() : null;
    }
}
