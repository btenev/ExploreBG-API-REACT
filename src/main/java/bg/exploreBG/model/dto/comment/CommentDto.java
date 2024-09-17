package bg.exploreBG.model.dto.comment;

import bg.exploreBG.model.dto.user.UserBasicInfo;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime lastUpdateDate,
        UserBasicInfo owner
) {
}
