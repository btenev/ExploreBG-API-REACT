package bg.exploreBG.model.dto.hike;

import bg.exploreBG.model.dto.CommentsDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;

import java.time.LocalDate;
import java.util.List;

public record HikeDetailsDto(
        Long id,
        LocalDate hikeDate,
        String hikeName,
        String hikeInfo,
        String nextTo,
        UserBasicInfo owner,
        HikingTrailDetailsDto hikingTrail,
        List<CommentsDto> comments) {
}
