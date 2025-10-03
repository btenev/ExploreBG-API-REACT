package bg.exploreBG.service;

import bg.exploreBG.model.dto.destination.single.*;
import bg.exploreBG.model.dto.destination.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.utils.EntityUpdateUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class DestinationUpdateService {
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final MainImageUpdater mainImageUpdater;
    private final EntityFieldUpdater entityFieldUpdater;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final GeometryFactory geometryFactory;

    public DestinationUpdateService(
            DestinationQueryBuilder destinationQueryBuilder,
            MainImageUpdater mainImageUpdater,
            EntityFieldUpdater entityFieldUpdater,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            GeometryFactory geometryFactory
    ) {
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.mainImageUpdater = mainImageUpdater;

        this.entityFieldUpdater = entityFieldUpdater;
        this.destinationPersistence = destinationPersistence;
        this.geometryFactory = geometryFactory;
    }

    public DestinationNameDto updateDestinationName(
            Long destinationId,
            DestinationUpdateDestinationNameDto dto,
            UserDetails user
    ) {
        return updateDestinationField(
                destinationId,
                user,
                dto.destinationName(),
                DestinationEntity::getDestinationName,
                DestinationEntity::setDestinationName,
                (destination, isUpdated) -> new DestinationNameDto(
                        destination.getDestinationName(),
                        EntityUpdateUtils.getModificationDateIfUpdated(destination, isUpdated)));
    }

    public DestinationLocationDto updateLocation(
            Long destinationId,
            DestinationUpdateLocationDto dto,
            UserDetails user
    ) {
        Point newPoint;
        if (dto.longitude() == null || dto.latitude() == null) {
            newPoint = null;
        } else {
            newPoint = geometryFactory.createPoint(new Coordinate(dto.longitude(), dto.latitude()));
        }
        return updateDestinationField(
                destinationId,
                user,
                newPoint,
                DestinationEntity::getLocation,
                DestinationEntity::setLocation,
                (destination, isUpdated) -> {
                    Point loc = destination.getLocation();
                    return new DestinationLocationDto(
                            loc != null ? loc.getX() : null, // longitude
                            loc != null ? destination.getLocation().getY() : null, // latitude
                            EntityUpdateUtils.getModificationDateIfUpdated(destination, isUpdated));
                });
    }

    public DestinationInfoDto updateDestinationInfo(
            Long destinationId,
            DestinationUpdateInfoDto dto,
            UserDetails user
    ) {
        return updateDestinationField(
                destinationId,
                user,
                dto.destinationInfo(),
                DestinationEntity::getDestinationInfo,
                DestinationEntity::setDestinationInfo,
                (destination, isUpdated) -> new DestinationInfoDto(
                        destination.getDestinationInfo(),
                        EntityUpdateUtils.getModificationDateIfUpdated(destination, isUpdated)));
    }

    public DestinationNextToDto updateNextTo(
            Long destinationId,
            DestinationUpdateNextToDto dto,
            UserDetails user
    ) {
        return updateDestinationField(
                destinationId,
                user,
                dto.nextTo(),
                DestinationEntity::getNextTo,
                DestinationEntity::setNextTo,
                (destination, isUpdated) -> new DestinationNextToDto(
                        destination.getNextTo(),
                        EntityUpdateUtils.getModificationDateIfUpdated(destination, isUpdated)));
    }

    public DestinationTypeDto updateType(
            Long destinationId,
            DestinationUpdateTypeDto dto,
            UserDetails user
    ) {
        return updateDestinationField(
                destinationId,
                user,
                dto.type(),
                DestinationEntity::getType,
                DestinationEntity::setType,
                (destination, isUpdated) -> new DestinationTypeDto(
                        destination.getType().getValue(),
                        EntityUpdateUtils.getModificationDateIfUpdated(destination, isUpdated)));
    }

    public long updateDestinationMainImage(
            Long destinationId,
            ImageMainUpdateDto dto,
            UserDetails userDetails,
            List<StatusEnum> statusList
    ) {
        DestinationEntity destination =
                this.destinationQueryBuilder
                        .getDestinationWithImagesByIdAndStatusIfOwner(
                                destinationId, statusList, userDetails.getUsername());

        return this.mainImageUpdater.updateMainImage(
                destination,
                dto,
                this.destinationPersistence::saveEntityWithReturn);
    }

    private <T, R> R updateDestinationField(
            Long destinationId,
            UserDetails user,
            T newValue,
            Function<DestinationEntity, T> getter,
            BiConsumer<DestinationEntity, T> setter,
            BiFunction<DestinationEntity, Boolean, R> dtoMapper
    ) {
        DestinationEntity destination =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, user.getUsername());

        return this.entityFieldUpdater.updateEntityField(
                destination,
                () -> getter.apply(destination),
                val -> setter.accept(destination, val),
                newValue,
                this.destinationPersistence::saveEntityWithReturn,
                dtoMapper);
    }
}
