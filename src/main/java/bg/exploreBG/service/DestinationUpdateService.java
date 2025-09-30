package bg.exploreBG.service;

import bg.exploreBG.model.dto.destination.single.*;
import bg.exploreBG.model.dto.destination.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.utils.ImageUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class DestinationUpdateService {
    private final DestinationQueryBuilder destinationQueryBuilder;

    private final EntityUpdateService entityUpdateService;

    private final GenericPersistenceService<DestinationEntity> destinationPersistence;

    private final GeometryFactory geometryFactory;

    public DestinationUpdateService(
            DestinationQueryBuilder destinationQueryBuilder,
            EntityUpdateService entityUpdateService,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            GeometryFactory geometryFactory
    ) {
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.entityUpdateService = entityUpdateService;
        this.destinationPersistence = destinationPersistence;
        this.geometryFactory = geometryFactory;
    }

    public DestinationNameDto updateDestinationName(
            Long destinationId,
            DestinationUpdateDestinationNameDto updateDestinationName,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getDestinationName,
                current::setDestinationName,
                updateDestinationName.destinationName(),
                (destination, isUpdated) -> new DestinationNameDto(
                        destination.getDestinationName(),
                        isUpdated ? destination.getModificationDate() : null));
    }

    public DestinationLocationDto updateLocation(
            Long destinationId,
            DestinationUpdateLocationDto updateLocation,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        Point newPoint;
        if (updateLocation.longitude() == null || updateLocation.latitude() == null) {
            newPoint = null;
        } else {
            newPoint = geometryFactory.createPoint(new Coordinate(updateLocation.longitude(), updateLocation.latitude()));
        }

        return updateDestinationField(
                current,
                current::getLocation,
                current::setLocation,
                newPoint,
                (destination, isUpdated) ->
                {
                    Point loc = destination.getLocation();
                    return new DestinationLocationDto(
                            loc != null ? loc.getX() : null, // longitude
                            loc != null ? destination.getLocation().getY() : null, // latitude
                            isUpdated ? destination.getModificationDate() : null);
                });
    }

    public DestinationInfoDto updateDestinationInfo(
            Long destinationId,
            DestinationUpdateInfoDto destinationUpdateInfo,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getDestinationInfo,
                current::setDestinationInfo,
                destinationUpdateInfo.destinationInfo(),
                (destination, isUpdated) -> new DestinationInfoDto(
                        destination.getDestinationInfo(),
                        isUpdated ? destination.getModificationDate() : null));
    }

    public DestinationNextToDto updateNextTo(
            Long destinationId,
            DestinationUpdateNextToDto updateNextTo,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getNextTo,
                current::setNextTo,
                updateNextTo.nextTo(),
                (destination, isUpdated) -> new DestinationNextToDto(
                        destination.getNextTo(),
                        isUpdated ? destination.getModificationDate() : null));
    }

    public DestinationTypeDto updateType(
            Long destinationId,
            DestinationUpdateTypeDto updateType,
            UserDetails userDetails
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndStatusIfOwner(destinationId, userDetails.getUsername());

        return updateDestinationField(
                current,
                current::getType,
                current::setType,
                updateType.type(),
                (destination, isUpdated) -> new DestinationTypeDto(
                        destination.getType().getValue(),
                        isUpdated ? destination.getModificationDate() : null));
    }

    public long updateDestinationMainImage(
            Long destinationId,
            ImageMainUpdateDto imageMainUpdate,
            UserDetails userDetails,
            List<StatusEnum> statusList
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationWithImagesByIdAndStatusIfOwner(
                                destinationId, statusList, userDetails.getUsername());

        ImageEntity found = ImageUtils.filterMainImage(current.getImages(), imageMainUpdate.imageId());

        boolean isUpdated = this.entityUpdateService
                .updateFieldIfDifferent(current::getMainImage, current::setMainImage, found);

        if (isUpdated) {
            this.destinationPersistence.saveEntityWithoutReturn(current);
        }

        return found.getId();
    }

    private <T, R> R updateDestinationField(
            DestinationEntity destination,
            Supplier<T> getter,
            Consumer<T> setter,
            T newValue,
            BiFunction<DestinationEntity, Boolean, R> dtoMapper
    ) {
        boolean isUpdated = this.entityUpdateService.updateFieldIfDifferent(getter, setter, newValue);
        destination = updateDestinationStatusAndSaveIfChanged(destination, isUpdated);
        return dtoMapper.apply(destination, isUpdated);
    }

    private DestinationEntity updateDestinationStatusAndSaveIfChanged(
            DestinationEntity destination,
            boolean isUpdated
    ) {
        if (isUpdated) {
            destination.setStatus(StatusEnum.PENDING);
            destination.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
            destination.setModificationDate(LocalDateTime.now());
            destination = this.destinationPersistence.saveEntityWithReturn(destination);
        }
        return destination;
    }
}
