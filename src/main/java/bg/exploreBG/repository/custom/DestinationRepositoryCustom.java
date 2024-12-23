package bg.exploreBG.repository.custom;

import bg.exploreBG.model.dto.destination.DestinationBasicLikesDto;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DestinationRepositoryCustom {
    Page<DestinationBasicLikesDto> getDestinationsWithLikes(
            StatusEnum detailsStatus,
            StatusEnum imageStatus,
            String email,
            Pageable pageable,
            Boolean sortByLikedUser);
}
