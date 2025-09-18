package bg.exploreBG.model.dto.accommodation.validate;

import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.FoodAvailabilityEnum;
import bg.exploreBG.model.validation.DescriptionField;
import bg.exploreBG.model.validation.ValidPlaceName;
import bg.exploreBG.updatable.UpdatableEntityDto;
import jakarta.validation.constraints.*;

public record AccommodationCreateOrReviewDto(
        @NotNull(message = "Please enter your accommodation name.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your accommodation name"
        )
        String accommodationName,

        @NotNull(message = "Please enter the village/town/city near your accommodation.")
        @ValidPlaceName(
                max = 20,
                min = 3,
                fieldName = "Your village/town/city name"
        )
        String nextTo,

        @Size(min = 10, max = 13, message = "Your phone number must be between 10 and 13 characters long.")
        @Pattern(
                regexp = "^(?:\\+359|0)(87|88|89|98|99)\\d{7}$",
                message = "Your phone number format is invalid. Must start with +359 or 0 and use operator codes 87, 88, 89, 98, 99."
        )
        String phoneNumber,

        @Pattern(
                regexp = "^(https?://).+$",
                message = "Please provide a valid URL starting with http:// or https://"
        )
        String site,

        @DescriptionField(max = 800)
        String accommodationInfo,

        @PositiveOrZero(message = "Bed capacity cannot be negative.")
        Integer bedCapacity,

        @DecimalMin(value = "0.01", inclusive = true, message = "Price per bed must be greater than 0.")
        Double pricePerBed,

        @NotNull(message = "Please specify if food is available.")
        FoodAvailabilityEnum availableFood,

        @NotNull(message = "Please specify the accessibility (On foot or By car).")
        AccessibilityEnum access,

        @NotNull(message = "Please specify the accommodation type.")
        AccommodationTypeEnum type
) implements UpdatableEntityDto<AccommodationEntity> {
}
