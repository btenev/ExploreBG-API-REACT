package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.entity.DestinationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DestinationMapper {
    @Mapping(source = "type.value", target = "type")
    DestinationDetailsDto destinationEntityToDestinationDetailsDto (DestinationEntity destinationEntity);
}
