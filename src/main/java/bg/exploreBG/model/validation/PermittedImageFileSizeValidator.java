package bg.exploreBG.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class PermittedImageFileSizeValidator implements ConstraintValidator<PermittedImageFileSize, MultipartFile> {
    /*
     4MB
     */
    private static final long MAX_FILE_SIZE = 4 * 1024 * 1024;
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        return !multipartFile.isEmpty() && multipartFile.getSize() <= MAX_FILE_SIZE;
    }
}
