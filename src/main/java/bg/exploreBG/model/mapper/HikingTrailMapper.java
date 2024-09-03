package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface HikingTrailMapper {

    @Mapping(target = "gpxUrl", source = "gpxFile.gpxUrl")
    HikingTrailDetailsDto hikingTrailEntityToHikingTrailDetailsDto(HikingTrailEntity hikingTrailEntity);

    HikingTrailReviewDto hikingTrailEntityToHikingTrailReviewDto(HikingTrailEntity hikingTrailEntity);

    @Mapping(target = "destinations", ignore = true)
    @Mapping(target = "availableHuts", ignore = true)
    HikingTrailEntity hikingTrailCreateDtoToHikingTrailEntity(HikingTrailCreateOrReviewDto hikingTrailCreateOrReviewDto);
}
