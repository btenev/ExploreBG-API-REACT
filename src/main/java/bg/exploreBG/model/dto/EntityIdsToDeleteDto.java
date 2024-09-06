package bg.exploreBG.model.dto;

import java.util.List;

public record EntityIdsToDeleteDto(
        String folder,
        List<Long> ids
) {
}
