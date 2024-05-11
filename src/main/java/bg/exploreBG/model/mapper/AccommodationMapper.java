package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.dto.AccommodationDetailsDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {

    AccommodationBasicPlusImageDto accommodationEntityToAccommodationBasicPlusImageDto(AccommodationEntity accommodation);
    AccommodationDetailsDto accommodationEntityToAccommodationDetailsDto(AccommodationEntity accommodationEntity);
}
