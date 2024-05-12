package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.dto.AccommodationDetailsDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccommodationMapper {

    AccommodationBasicPlusImageDto accommodationEntityToAccommodationBasicPlusImageDto(AccommodationEntity accommodation);

    @Mapping(source = "access.value", target = "access")
    @Mapping(source = "type.value", target = "type")
    AccommodationDetailsDto accommodationEntityToAccommodationDetailsDto(AccommodationEntity accommodationEntity);
}
