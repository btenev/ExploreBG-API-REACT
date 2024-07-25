package bg.exploreBG.model.dto.hikingTrail.validate;

import bg.exploreBG.deserializer.SuitableForEnumDeserializer;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;

import java.util.List;

public record HikingTrailCreateDto(
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
                regexp = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@]*$",
                message = "err-trail-info-regex"
        )
        @Size(
                max = 3000,
                message = "err-trail-info-max-length"
        )
        String trailInfo,

//      @EnumMatch(enumClass = SeasonEnum.class)
        SeasonEnum seasonVisited,

        WaterAvailabilityEnum waterAvailable,

      /*
        @EnumMatch(enumClass = DifficultyLevelEnum.class)
        @NotNull(message = "Please, rate the difficulty level of the trail with a number from 1 to 6!")
      */
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

        List<DestinationIdDto> destinations,

        List<AccommodationIdDto> availableHuts
) {
}



