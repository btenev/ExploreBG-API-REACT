package bg.exploreBG.repository.custom;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HikingTrailRepositoryCustom {
    Page<HikingTrailBasicLikesDto> getTrailsWithLikes(StatusEnum statusEnum, String email, Pageable pageable, Boolean sortByLikedUser);
}
