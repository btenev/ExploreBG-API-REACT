package bg.exploreBG.model.dto;

import java.util.List;

public record ApiResponseCollection<T>(
        List<T> data
) {
}
