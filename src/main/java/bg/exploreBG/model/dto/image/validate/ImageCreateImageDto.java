package bg.exploreBG.model.dto.image.validate;

import jakarta.validation.constraints.NotBlank;

public record ImageCreateImageDto(
   @NotBlank(message = "Please, enter image name!")
   String name,
   @NotBlank(message = "Please, enter folder name!")
   String folder

) {
}
