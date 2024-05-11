package bg.exploreBG.model.dto;

import java.util.List;

public record AccommodationDetailsDto(Long id,
                                      String accommodationName,
                                      UserBasicInfo owner,
                                      String phoneNumber,
                                      String site,
                                      String picturesUrl,
                                      String accommodationInfo,
                                      Integer bedCapacity,
                                      Double pricePerBed,
                                      Boolean foodAvailable,
                                      String access,
                                      String type,
                                      String nextTo,
                                      List<CommentsDto> comments) {
}
