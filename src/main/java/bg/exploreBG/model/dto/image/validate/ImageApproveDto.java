package bg.exploreBG.model.dto.image.validate;

import java.util.Set;

public record ImageApproveDto(
        Set<Long> imageIds
) {
}
