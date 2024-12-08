package bg.exploreBG.repository.custom;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicLikesDto;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccommodationRepositoryCustom {
    Page<AccommodationBasicLikesDto> getAccommodationsWithLikes(
            StatusEnum detailsStatus,
            StatusEnum imageStatus,
            String email,
            Pageable pageable,
            Boolean sortByLikedUser);
}
