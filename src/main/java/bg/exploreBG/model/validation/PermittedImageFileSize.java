package bg.exploreBG.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PermittedImageFileSizeValidator.class)
public @interface PermittedImageFileSize {
    String message() default "File size should be bigger then 0 and less than 4 MB";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
