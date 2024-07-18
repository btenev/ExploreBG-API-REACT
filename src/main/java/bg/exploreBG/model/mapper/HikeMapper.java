package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeDetailsDto;
import bg.exploreBG.model.dto.hike.validate.HikeCreateDto;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.model.enums.SuitableForEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HikeMapper {
    @Mapping(target = "hikeName", expression = "java(hikeEntity.getStartPoint() + ' ' + '-' + ' ' + hikeEntity.getEndPoint())")
    HikeBasicDto hikeEntityToHikeBasicDto(HikeEntity hikeEntity);

    @Mapping(source = "hikingTrail.trailDifficulty.level", target = "hikingTrail.trailDifficulty")
    @Mapping(source = "hikingTrail.seasonVisited.value", target = "hikingTrail.seasonVisited")
    @Mapping(source = "hikingTrail.activity", target = "hikingTrail.activity")
    @Mapping(target = "hikeName", expression = "java(hikeEntity.getStartPoint() + ' ' + '-' + ' ' + hikeEntity.getEndPoint())")
    HikeDetailsDto hikeEntityToHikeDetailsDto(HikeEntity hikeEntity);

    @Mapping(target = "hikingTrail", ignore = true)
    HikeEntity hikeCreateDtoToHikeEntity(HikeCreateDto hikeCreateDto);

    default List<String> suitableForEnumToStringValue (List<SuitableForEnum> activities) {
        return activities
                .stream()
                .map(SuitableForEnum::getValue)
                .toList();
    }
}
