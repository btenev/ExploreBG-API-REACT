package bg.exploreBG.model.dto.accommodation;

import bg.exploreBG.model.dto.CommentsDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;

import java.util.List;

public record AccommodationDetailsDto(
        Long id,
        String accommodationName,
        UserBasicInfo owner,
        String phoneNumber,
        String site,
        String imageUrl,
        String accommodationInfo,
        Integer bedCapacity,
        Double pricePerBed,
        Boolean foodAvailable,
        String access,
        String type,
        String nextTo,
        List<CommentsDto> comments
) {
}
