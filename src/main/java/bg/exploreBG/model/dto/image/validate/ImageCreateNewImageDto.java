package bg.exploreBG.model.dto.image.validate;

import bg.exploreBG.model.validation.PermittedImageFileFormat;
import bg.exploreBG.model.validation.PermittedImageFileSize;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record ImageCreateNewImageDto(
   @NotBlank(message = "Please, enter image name!")
   String name,
   @NotBlank(message = "Please, enter folder name!")
   String folder

) {
}
