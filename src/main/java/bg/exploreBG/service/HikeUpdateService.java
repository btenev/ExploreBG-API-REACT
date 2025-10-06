package bg.exploreBG.service;

import bg.exploreBG.model.dto.EntityInfoDto;
import bg.exploreBG.model.dto.NextToDto;
import bg.exploreBG.model.dto.hike.single.HikeDateDto;
import bg.exploreBG.model.dto.hike.validate.HikeUpdateDateDto;
import bg.exploreBG.model.dto.hike.validate.HikeUpdateInfoDto;
import bg.exploreBG.model.dto.hike.validate.HikeUpdateNextToDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailEndPointDto;
import bg.exploreBG.model.dto.hikingTrail.single.HikingTrailStartPointDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateEndPointDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailUpdateStartPointDto;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.querybuilder.HikeQueryBuilder;
import bg.exploreBG.utils.EntityUpdateUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class HikeUpdateService {
    private final HikeQueryBuilder hikeQueryBuilder;
    private final EntityFieldUpdater entityFieldUpdater;
    private final GenericPersistenceService<HikeEntity> hikePersistence;

    public HikeUpdateService(
            HikeQueryBuilder hikeQueryBuilder,
            EntityFieldUpdater entityFieldUpdater,
            GenericPersistenceService<HikeEntity> hikePersistence
    ) {
        this.hikeQueryBuilder = hikeQueryBuilder;
        this.entityFieldUpdater = entityFieldUpdater;
        this.hikePersistence = hikePersistence;
    }

    public HikingTrailStartPointDto updateHikeStartPoint(
            Long hikeId,
            HikingTrailUpdateStartPointDto dto,
            UserDetails user
    ) {
        return updateHikeField(
                hikeId,
                user,
                dto.startPoint(),
                HikeEntity::getStartPoint,
                HikeEntity::setStartPoint,
                (hike, isUpdated) -> new HikingTrailStartPointDto(
                        hike.getStartPoint(),
                        EntityUpdateUtils.getModificationDateIfUpdated(hike, isUpdated)));
    }

    public HikingTrailEndPointDto updateHikeEndPoint(
            Long hikeId,
            HikingTrailUpdateEndPointDto dto,
            UserDetails user
    ) {
        return updateHikeField(
                hikeId,
                user,
                dto.endPoint(),
                HikeEntity::getEndPoint,
                HikeEntity::setEndPoint,
                (hike, isUpdated) -> new HikingTrailEndPointDto(
                        hike.getEndPoint(),
                        EntityUpdateUtils.getModificationDateIfUpdated(hike, isUpdated)));
    }

    public HikeDateDto updateHikeDate(
            Long hikeId,
            HikeUpdateDateDto dto,
            UserDetails user
    ) {
        return updateHikeField(
                hikeId,
                user,
                dto.hikeDate(),
                HikeEntity::getHikeDate,
                HikeEntity::setHikeDate,
                (hike, isUpdated) -> new HikeDateDto(
                        hike.getHikeDate(),
                        EntityUpdateUtils.getModificationDateIfUpdated(hike, isUpdated)));
    }

    public NextToDto updateHikeNextTo(
            Long hikeId,
            HikeUpdateNextToDto dto,
            UserDetails user
    ) {
        return updateHikeField(
                hikeId,
                user,
                dto.nextTo(),
                HikeEntity::getNextTo,
                HikeEntity::setNextTo,
                (hike, isUpdated) -> new NextToDto(
                        hike.getNextTo(),
                        EntityUpdateUtils.getModificationDateIfUpdated(hike, isUpdated)));
    }

    public EntityInfoDto updateHikeInfo(
            Long hikeId,
            HikeUpdateInfoDto dto,
            UserDetails user
    ) {
        return updateHikeField(
                hikeId,
                user,
                dto.hikeInfo(),
                HikeEntity::getHikeInfo,
                HikeEntity::setHikeInfo,
                (hike, isUpdated) -> new EntityInfoDto(
                        hike.getHikeInfo(),
                        EntityUpdateUtils.getModificationDateIfUpdated(hike, isUpdated)));
    }

    public <T, R> R updateHikeField(
            Long hikeId,
            UserDetails user,
            T newValue,
            Function<HikeEntity, T> getter,
            BiConsumer<HikeEntity, T> setter,
            BiFunction<HikeEntity, Boolean, R> dtoMapper
    ) {
        HikeEntity hike =
                this.hikeQueryBuilder.getHikeByIdIfOwner(hikeId, user.getUsername());

        return this.entityFieldUpdater.updateEntityFieldBasic(
                hike,
                () -> getter.apply(hike),
                val -> setter.accept(hike, val),
                newValue,
                this.hikePersistence::saveEntityWithReturn,
                dtoMapper);
    }
}


