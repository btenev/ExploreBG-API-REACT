package bg.exploreBG.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class PermittedImageFileFormatValidator implements ConstraintValidator<PermittedImageFileFormat, MultipartFile> {
    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        String contentType = multipartFile.getContentType();

        return contentType != null && isSupportedContentType(contentType);
    }

    private boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/jpeg") ||
               contentType.equals("image/gif");
    }
}
