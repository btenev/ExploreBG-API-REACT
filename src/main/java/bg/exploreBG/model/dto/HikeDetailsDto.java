package bg.exploreBG.model.dto;

import java.time.LocalDate;
import java.util.List;

public record HikeDetailsDto(Long id,
                             LocalDate hikeDate,
                             String hikeName,
                             String hikeInfo,
                             String nextTo,
                             UserBasicInfo owner,
                             HikingTrailDetailsDto hikingTrail,
                             List<CommentsDto> comments) {
}
