package bg.exploreBG.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {PermittedImageFileFormatValidator.class})
public @interface PermittedImageFileFormat {
    String message() default "Only GIF, PNG or JPG images are allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
