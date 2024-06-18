package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UpdateUserBirthdate(
        @NotNull(message = "Please, enter your birthdate!")
        @Past(message = "Date should be in the past!")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birthdate
) {
}
