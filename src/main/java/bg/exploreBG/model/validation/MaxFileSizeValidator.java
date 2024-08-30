package bg.exploreBG.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, MultipartFile> {
    private Logger logger = LoggerFactory.getLogger(MaxFileSizeValidator.class);
    private long maxSize;

    @Override
    public void initialize(MaxFileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize() * 1024 * 1024;
        logger.info("Max size {}", maxSize);
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        if (multipartFile.isEmpty()) {
            return false;
        }

        boolean isValid = multipartFile.getSize() <= maxSize;
        logger.info("Size is {}", isValid);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "The file size must be less than or equal to " + (maxSize / 1024 / 1024) + " MB.")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
