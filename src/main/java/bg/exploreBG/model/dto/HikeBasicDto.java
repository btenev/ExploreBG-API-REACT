package bg.exploreBG.model.dto;

import java.time.LocalDate;

public record HikeBasicDto(Long id,
                           String hikeName,
                           LocalDate hikeDate,
                           String imageUrl,
                           String hikeInfo) {
}
