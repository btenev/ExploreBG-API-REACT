package bg.exploreBG.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DescriptionFieldValidator implements ConstraintValidator<DescriptionField, String> {
    private int maxLength;
    private static final String PATTERN = "^[a-zA-Z0-9\\-.,\\s\\n()'`:;?!@\"]*$";

    @Override
    public void initialize(DescriptionField constraintAnnotation) {
        this.maxLength = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 1️⃣ Null or empty check
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Please provide your description.")
                    .addConstraintViolation();
            return false;
        }

        // 2️⃣ Check pattern
        if (!value.matches(PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Your description can only contain letters (A-Z, a-z), numbers (0-9), spaces, new lines, and these symbols: - . , ( ) ' ` \" : ; ? ! @"
            ).addConstraintViolation();
            return false;
        }

        // 3️⃣ Check max length
        if (value.length() > maxLength) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    String.format("Your description must not exceed %d characters.", maxLength)
            ).addConstraintViolation();
            return false;
        }

        return true;
    }
}
