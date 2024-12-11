package bg.exploreBG.model.dto.accommodation.validate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record AccommodationUpdateNextToDto(
        @NotNull(message = "Please, enter town or city name that is close to the trail!")
        @Pattern(
                regexp = "^[A-Za-z]{3,15}$",
                message = "City/town name should contain from 3 to 15 letters!"
        )
        String nextTo,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate
) {
}
