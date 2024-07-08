package bg.exploreBG.model.dto.destination;

import bg.exploreBG.model.dto.CommentsDto;

import java.util.List;

public record DestinationDetailsDto(
        Long id,
        String destinationName,
        String location,
        String destinationInfo,
        String imageUrl,
        String nextTo,
        String type,
        List<CommentsDto> comments
) {
}





