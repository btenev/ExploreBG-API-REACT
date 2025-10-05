package bg.exploreBG.model.dto.hike;

import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record HikeDetailsDto(
        Long id,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime hikeDate,
        String hikeName,
        String hikeInfo,
        String nextTo,
        UserBasicInfo owner,
        HikingTrailDetailsDto hikingTrail,
        List<CommentDto> comments) {
}
