package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeBasicOwnerDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.user.UserBasicInfo;
import bg.exploreBG.model.dto.user.UserDataDto;
import bg.exploreBG.model.dto.user.UserDetailsDto;
import bg.exploreBG.model.dto.user.UserDetailsOwnerDto;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "imageUrl", source = "user.userImage.imageUrl")
    UserBasicInfo userEntityToUserBasicInfo(UserEntity user);

    @Mapping(source = "user.userImage.imageUrl", target = "imageUrl")
    @Mapping(source = "user.createdHikes", target = "createdHikes", qualifiedByName = "mapToHikeBasicDto")
    UserDetailsDto userEntityToUserDetailsDto(UserEntity user);

    @Mapping(source = "user.userImage.imageUrl", target = "imageUrl")
    @Mapping(source = "user.createdHikes", target = "createdHikes", qualifiedByName = "mapToHikeBasicOwnerDto")
    @Mapping(source = "user.createdTrails", target = "createdTrails", qualifiedByName = "mapToHikingTrailBasicDto")
    UserDetailsOwnerDto userEntityToUserDetailsOwnerDto(UserEntity user);

//    @Mapping(source = "user.userImage.imageUrl", target = "imageUrl")
//    UserDataDto userEntityToUserDataDto(UserEntity user);

    @Named("mapToHikeBasicDto")
    default List<HikeBasicDto> mapToHikeBasicDto(List<HikeEntity> createdHikes) {
        return createdHikes.stream()
                .map(h -> new HikeBasicDto(
                        h.getId(),
                        String.format("%s - %s", h.getStartPoint(), h.getEndPoint()),
                        h.getHikeDate(),
                        h.getImageUrl(),
                        h.getHikeInfo()))
                .collect(Collectors.toList());
    }

    @Named("mapToHikeBasicOwnerDto")
    default List<HikeBasicOwnerDto> mapToHikeBasicOwnerDto(List<HikeEntity> createdHikes) {
        return createdHikes.stream()
                .map(h -> new HikeBasicOwnerDto(
                        h.getId(),
                        String.format("%s - %s", h.getStartPoint(), h.getEndPoint()),
                        h.getHikeDate(),
                        h.getImageUrl()
                )).collect(Collectors.toList());
    }

    @Named("mapToHikingTrailBasicDto")
    default List<HikingTrailBasicDto> mapToHikingTrailBasicDto(List<HikingTrailEntity> createdTrails) {
        return createdTrails.stream()
                .map(t -> new HikingTrailBasicDto(
                        t.getId(),
                        String.format("%s - %s", t.getStartPoint(), t.getEndPoint()),
                        t.getTrailInfo(),
                        t.getMainImage() != null ? t.getMainImage().getImageUrl() : null
                )).collect(Collectors.toList());
    }
}
