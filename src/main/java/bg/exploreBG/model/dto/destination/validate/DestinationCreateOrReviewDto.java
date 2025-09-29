package bg.exploreBG.model.dto.destination.validate;

import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.enums.DestinationTypeEnum;
import bg.exploreBG.model.validation.DescriptionField;
import bg.exploreBG.model.validation.ValidPlaceName;
import bg.exploreBG.updatable.UpdatableEntityDto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DestinationCreateOrReviewDto(
        @NotNull(message = "Please enter your destination name.")
        @ValidPlaceName(
                max = 30,
                min = 3,
                fieldName = "Your destination name"
        )
        String destinationName,

        @DecimalMin(value = "-90.0", message = "Your latitude cannot be less than -90.")
        @DecimalMax(value = "90.0", message = "Your latitude cannot be greater than 90.")
        Double latitude,

        @DecimalMin(value = "-180.0", message = "Your longitude cannot be less than -180.")
        @DecimalMax(value = "180.0", message = "Your longitude cannot be greater than 180.")
        Double longitude,

        @DescriptionField(max = 800)
        String destinationInfo,

        @NotNull(message = "Please enter the village/town/city near your destination.")
        @ValidPlaceName(
                max = 20,
                min = 3,
                fieldName = "Your village/town/city name"
        )
        String nextTo,

        @NotNull(message = "Please specify the destination type.")
        DestinationTypeEnum type
) implements UpdatableEntityDto<DestinationEntity> {
}
