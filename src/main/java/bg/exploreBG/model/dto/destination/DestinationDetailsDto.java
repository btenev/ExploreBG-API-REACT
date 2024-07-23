package bg.exploreBG.model.dto.destination;

import bg.exploreBG.model.dto.comment.CommentDto;

import java.util.List;

public record DestinationDetailsDto(
        Long id,
        String destinationName,
        String location,
        String destinationInfo,
        String imageUrl,
        String nextTo,
        String type,
        List<CommentDto> comments
) {
}





