package bg.exploreBG.model.validation;

import bg.exploreBG.utils.ValidationMessages;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPlaceNameValidator implements ConstraintValidator<ValidPlaceName, String> {
    private int min;
    private int max;
    private String pattern;
    private String fieldName;

    @Override
    public void initialize(ValidPlaceName constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.pattern = constraintAnnotation.pattern();
        this.fieldName = constraintAnnotation.fieldName();
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle the message if needed
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (value.length() < min || value.length() > max) {
            context.buildConstraintViolationWithTemplate(
                    fieldName + " must be between " + min + " and " + max + " characters long."
            ).addConstraintViolation();
            isValid = false;
        }

        if (!value.matches(pattern)) {
            context.buildConstraintViolationWithTemplate(
                    ValidationMessages.TEXT_PATTERN_GENERIC
            ).addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
