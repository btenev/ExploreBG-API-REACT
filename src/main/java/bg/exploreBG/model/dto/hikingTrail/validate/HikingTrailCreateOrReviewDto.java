package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.deserializer.SuitableForEnumDeserializer;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import bg.exploreBG.updatable.UpdatableEntityDto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.Set;

public record HikingTrailCreateOrReviewDto(
        @NotNull(message = "err-start-point-required")
        @Pattern(
                regexp = "^[A-Za-z]+(\\s?[A-Za-z]+)*$",
                message = "err-place-regex"
        )
        @Size(
                max = 30,
                min = 3,
                message = "err-place-length"
        )
        String startPoint,

        @NotNull(message = "err-end-point-required")
        @Pattern(
                regexp = "^[A-Za-z]+(\\s?[A-Za-z]+)*$",
                message = "err-place-regex"
        )
        @Size(
                max = 30,
                min = 3,
                message = "err-place-length"
        )
        String endPoint,

        @Positive(message = "err-total-distance")
        Double totalDistance,

        @NotNull(message = "err-trail-info-required")
        @Pattern(
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@\"]*$",
                message = "err-trail-info-regex"
        )
        @Size(
                max = 3000,
                message = "err-trail-info-max-length"
        )
        String trailInfo,

        SeasonEnum seasonVisited,

        WaterAvailabilityEnum waterAvailable,

        DifficultyLevelEnum trailDifficulty,

        @JsonDeserialize(using = SuitableForEnumDeserializer.class)
        List<SuitableForEnum> activity,

        @Positive(message = "err-total-elevation")
        Integer elevationGained,

        @NotNull(message = "err-next-to-required")
        @Pattern(
                regexp = "^[A-Za-z]+(\\s?[A-Za-z]+)*$",
                message = "err-place-regex"
        )
        @Size(
                max = 30,
                min = 3,
                message = "err-place-length")
        String nextTo,

        Set<DestinationIdDto> destinations,

        Set<AccommodationIdDto> availableHuts
) implements UpdatableEntityDto<HikingTrailEntity> {
}



