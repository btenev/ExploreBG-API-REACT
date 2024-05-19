package bg.exploreBG.model.dto;

import bg.exploreBG.model.enums.DestinationTypeEnum;
import jakarta.persistence.*;

import java.util.List;

public record DestinationDetailsDto(Long id,
                                    String destinationName,
                                    String location,
                                    String destinationInfo,
                                    String imageUrl,
                                    String nextTo,
                                    String type,
                                    List<CommentsDto> comments) {

}





