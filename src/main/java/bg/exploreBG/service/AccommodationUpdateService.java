package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.single.*;
import bg.exploreBG.model.dto.accommodation.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
import bg.exploreBG.utils.EntityUpdateUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class AccommodationUpdateService {
    private final MainImageUpdater mainImageUpdater;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final EntityFieldUpdater entityFieldUpdater;

    public AccommodationUpdateService(
            MainImageUpdater mainImageUpdater,
            AccommodationQueryBuilder accommodationQueryBuilder,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            EntityFieldUpdater entityFieldUpdater
    ) {
        this.mainImageUpdater = mainImageUpdater;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.accommodationPersistence = accommodationPersistence;
        this.entityFieldUpdater = entityFieldUpdater;
    }

    public AccommodationNameDto updateAccommodationName(
            Long accommodationId,
            AccommodationUpdateAccommodationNameDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.accommodationName(),
                AccommodationEntity::getAccommodationName,
                AccommodationEntity::setAccommodationName,
                (accommodation, isUpdated) -> new AccommodationNameDto(
                        accommodation.getAccommodationName(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationPhoneNumberDto updateAccommodationPhoneNumber(
            Long accommodationId,
            AccommodationUpdatePhoneNumberDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.phoneNumber(),
                AccommodationEntity::getPhoneNumber,
                AccommodationEntity::setPhoneNumber,
                (accommodation, isUpdated) -> new AccommodationPhoneNumberDto(
                        accommodation.getPhoneNumber(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationSiteDto updateAccommodationSite(
            Long accommodationId,
            AccommodationUpdateSiteDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.site(),
                AccommodationEntity::getSite,
                AccommodationEntity::setSite,
                (accommodation, isUpdated) -> new AccommodationSiteDto(
                        accommodation.getSite(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationInfoDto updateAccommodationInfo(
            Long accommodationId,
            AccommodationUpdateInfoDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.accommodationInfo(),
                AccommodationEntity::getAccommodationInfo,
                AccommodationEntity::setAccommodationInfo,
                (accommodation, isUpdated) -> new AccommodationInfoDto(
                        accommodation.getAccommodationInfo(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationBedCapacityDto updateAccommodationBedCapacity(
            Long accommodationId,
            AccommodationUpdateBedCapacityDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.bedCapacity(),
                AccommodationEntity::getBedCapacity,
                AccommodationEntity::setBedCapacity,
                (accommodation, isUpdated) -> new AccommodationBedCapacityDto(
                        accommodation.getBedCapacity(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationAvailableFoodDto updateAccommodationAvailableFood(
            Long accommodationId,
            AccommodationUpdateAvailableFoodDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.availableFood(),
                AccommodationEntity::getAvailableFood,
                AccommodationEntity::setAvailableFood,
                (accommodation, isUpdated) -> new AccommodationAvailableFoodDto(
                        accommodation.getAvailableFood().getValue(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationPricePerBedDto updateAccommodationPricePerBed(
            Long accommodationId,
            AccommodationUpdatePricePerBed dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.pricePerBed(),
                AccommodationEntity::getPricePerBed,
                AccommodationEntity::setPricePerBed,
                (accommodation, isUpdated) -> new AccommodationPricePerBedDto(
                        accommodation.getPricePerBed(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationAccessibilityDto updateAccommodationAccessibility(
            Long accommodationId,
            AccommodationUpdateAccessibilityDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.access(),
                AccommodationEntity::getAccess,
                AccommodationEntity::setAccess,
                (accommodation, isUpdated) -> new AccommodationAccessibilityDto(
                        accommodation.getAccess().getValue(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationNextToDto updateAccommodationNextTo(
            Long accommodationId,
            AccommodationUpdateNextToDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.nextTo(),
                AccommodationEntity::getNextTo,
                AccommodationEntity::setNextTo,
                (accommodation, isUpdated) -> new AccommodationNextToDto(
                        accommodation.getNextTo(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public AccommodationTypeDto updateAccommodationType(
            Long accommodationId,
            AccommodationUpdateTypeDto dto,
            UserDetails user
    ) {
        return updateAccommodationField(
                accommodationId,
                user,
                dto.type(),
                AccommodationEntity::getType,
                AccommodationEntity::setType,
                (accommodation, isUpdated) -> new AccommodationTypeDto(
                        accommodation.getType().getValue(),
                        EntityUpdateUtils.getModificationDateIfUpdated(accommodation, isUpdated)));
    }

    public long updateAccommodationMainImage(
            Long accommodationId,
            ImageMainUpdateDto dto,
            UserDetails userDetails,
            List<StatusEnum> statusList
    ) {
        AccommodationEntity accommodation =
                this.accommodationQueryBuilder
                        .getAccommodationWithImagesByIdAndStatusIfOwner(
                                accommodationId, statusList, userDetails.getUsername());

        return this.mainImageUpdater.updateMainImage(
                accommodation,
                dto,
                this.accommodationPersistence::saveEntityWithReturn);
    }

    private <T, R> R updateAccommodationField(
            Long accommodationId,
            UserDetails user,
            T newValue,
            Function<AccommodationEntity, T> getter,
            BiConsumer<AccommodationEntity, T> setter,
            BiFunction<AccommodationEntity, Boolean, R> dtoMapper
    ) {
        AccommodationEntity accommodation =
                this.accommodationQueryBuilder.
                        getAccommodationByIdAndStatusIfOwner(accommodationId, user.getUsername());

        return this.entityFieldUpdater.updateEntityField(
                accommodation,
                () -> getter.apply(accommodation),
                val -> setter.accept(accommodation, val),
                newValue,
                this.accommodationPersistence::saveEntityWithReturn,
                dtoMapper);
    }
}
