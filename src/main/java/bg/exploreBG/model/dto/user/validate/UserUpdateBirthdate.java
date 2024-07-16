package bg.exploreBG.model.dto.user.validate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UserUpdateBirthdate(
        //TODO: come up with validation for data when the format is not write, currently app returns unauthorised path
        @NotNull(message = "Please, enter your birthdate!")
        @Past(message = "Date should be in the past!")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate birthdate
) {
}
