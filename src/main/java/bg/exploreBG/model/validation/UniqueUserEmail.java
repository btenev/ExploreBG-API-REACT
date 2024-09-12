package bg.exploreBG.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUserEmailValidator.class)
public @interface UniqueUserEmail {
    String message() default "Invalid Email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
