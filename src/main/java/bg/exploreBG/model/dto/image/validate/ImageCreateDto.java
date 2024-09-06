package bg.exploreBG.model.dto.image.validate;

import jakarta.validation.constraints.NotBlank;

public record ImageCreateDto(

   @NotBlank(message = "Please, enter folder name!")
   String folder

) {
}
