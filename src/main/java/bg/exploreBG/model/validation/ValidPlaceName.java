package bg.exploreBG.model.validation;

import bg.exploreBG.utils.RegexUtils;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidPlaceNameValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPlaceName {
    String message() default "Invalid place name";

    int min() default 3;

    int max() default 30;

    String fieldName() default "This field";

    String pattern() default RegexUtils.PLACE_REGEX;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
