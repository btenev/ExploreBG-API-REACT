package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.single.*;
import bg.exploreBG.model.dto.accommodation.validate.*;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
import bg.exploreBG.utils.ImageUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class AccommodationUpdateService {
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final EntityUpdateService entityUpdateService;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;

    public AccommodationUpdateService(
            AccommodationQueryBuilder accommodationQueryBuilder,
            EntityUpdateService entityUpdateService,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence
    ) {
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.entityUpdateService = entityUpdateService;
        this.accommodationPersistence = accommodationPersistence;
    }

    public AccommodationNameDto updateAccommodationName(
            Long accommodationId,
            AccommodationUpdateAccommodationNameDto updateAccommodationName,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAccommodationName,
                current::setAccommodationName,
                updateAccommodationName.accommodationName(),
                (accommodation, isUpdated) -> new AccommodationNameDto(
                        accommodation.getAccommodationName(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationPhoneNumberDto updateAccommodationPhoneNumber(
            Long accommodationId,
            AccommodationUpdatePhoneNumberDto updatePhoneNumber,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getPhoneNumber,
                current::setPhoneNumber,
                updatePhoneNumber.phoneNumber(),
                (accommodation, isUpdated) -> new AccommodationPhoneNumberDto(
                        accommodation.getPhoneNumber(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationSiteDto updateAccommodationSite(
            Long accommodationId,
            AccommodationUpdateSiteDto updateSite,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getSite,
                current::setSite,
                updateSite.site(),
                (accommodation, isUpdated) -> new AccommodationSiteDto(
                        accommodation.getSite(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationInfoDto updateAccommodationInfo(
            Long accommodationId,
            AccommodationUpdateInfoDto updateInfo,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAccommodationInfo,
                current::setAccommodationInfo,
                updateInfo.accommodationInfo(),
                (accommodation, isUpdated) -> new AccommodationInfoDto(
                        accommodation.getAccommodationInfo(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationBedCapacityDto updateAccommodationBedCapacity(
            Long accommodationId,
            AccommodationUpdateBedCapacityDto updateBedCapacity,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getBedCapacity,
                current::setBedCapacity,
                updateBedCapacity.bedCapacity(),
                (accommodation, isUpdated) -> new AccommodationBedCapacityDto(
                        accommodation.getBedCapacity(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationAvailableFoodDto updateAccommodationAvailableFood(
            Long accommodationId,
            AccommodationUpdateAvailableFoodDto updateAvailableFood,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAvailableFood,
                current::setAvailableFood,
                updateAvailableFood.availableFood(),
                (accommodation, isUpdated) -> new AccommodationAvailableFoodDto(
                        accommodation.getAvailableFood().getValue(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationPricePerBedDto updateAccommodationPricePerBed(
            Long accommodationId,
            AccommodationUpdatePricePerBed updatePricePerBed,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getPricePerBed,
                current::setPricePerBed,
                updatePricePerBed.pricePerBed(),
                (accommodation, isUpdated) -> new AccommodationPricePerBedDto(
                        accommodation.getPricePerBed(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationAccessibilityDto updateAccommodationAccessibility(
            Long accommodationId,
            AccommodationUpdateAccessibilityDto accessibility,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getAccess,
                current::setAccess,
                accessibility.access(),
                (accommodation, isUpdated) -> new AccommodationAccessibilityDto(
                        accommodation.getAccess().getValue(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationNextToDto updateAccommodationNextTo(
            Long accommodationId,
            AccommodationUpdateNextToDto nextTo,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getNextTo,
                current::setNextTo,
                nextTo.nextTo(),
                (accommodation, isUpdated) -> new AccommodationNextToDto(
                        accommodation.getNextTo(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    public AccommodationTypeDto updateAccommodationType(
            Long accommodationId,
            AccommodationUpdateTypeDto accommodationType,
            UserDetails userDetails
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndStatusIfOwner(accommodationId, userDetails.getUsername());

        return updateAccommodationField(
                current,
                current::getType,
                current::setType,
                accommodationType.type(),
                (accommodation, isUpdated) -> new AccommodationTypeDto(
                        accommodation.getType().getValue(),
                        isUpdated ? accommodation.getModificationDate() : null));
    }

    private <T, R> R updateAccommodationField(
            AccommodationEntity accommodation,
            Supplier<T> getter,
            Consumer<T> setter,
            T newValue,
            BiFunction<AccommodationEntity, Boolean, R> dtoMapper
    ) {
        boolean isUpdated = this.entityUpdateService.updateFieldIfDifferent(getter, setter, newValue);
        accommodation = updateAccommodationStatusAndSaveIfChanged(accommodation, isUpdated);
        return dtoMapper.apply(accommodation, isUpdated);
    }

    private AccommodationEntity updateAccommodationStatusAndSaveIfChanged(
            AccommodationEntity accommodation,
            boolean isUpdated
    ) {
        if (isUpdated) {
            accommodation.setStatus(StatusEnum.PENDING);
            accommodation.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
            accommodation.setModificationDate(LocalDateTime.now());
            accommodation = this.accommodationPersistence.saveEntityWithReturn(accommodation);
        }
        return accommodation;
    }

    public long updateAccommodationMainImage(
            Long accommodationId,
            ImageMainUpdateDto dto,
            UserDetails userDetails,
            List<StatusEnum> statusList
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationWithImagesByIdAndStatusIfOwner(
                                accommodationId, statusList, userDetails.getUsername());

        ImageEntity found = ImageUtils.filterMainImage(current.getImages(), dto.imageId());

        boolean isUpdated =
                this.entityUpdateService
                        .updateFieldIfDifferent(current::getMainImage, current::setMainImage, found);

        if (isUpdated) {
            this.accommodationPersistence.saveEntityWithoutReturn(current);
        }

        return found.getId();
    }
}
