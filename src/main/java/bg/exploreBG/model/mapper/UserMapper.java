package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeBasicOwnerDto;
import bg.exploreBG.model.dto.user.UserDataDto;
import bg.exploreBG.model.dto.user.UserDetailsDto;
import bg.exploreBG.model.dto.user.UserDetailsOwnerDto;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "userImage.imageUrl", target = "imageUrl")
    @Mapping(source = "user.createdHikes", target = "createdHikes", qualifiedByName = "mapToHikeBasicDto")
    UserDetailsDto userEntityToUserDetailsDto(UserEntity user);

    @Mapping(source = "userImage.imageUrl", target = "imageUrl")
    @Mapping(source = "user.createdHikes", target = "createdHikes", qualifiedByName = "mapToHikeBasicOwnerDto")
    UserDetailsOwnerDto userEntityToUserDetailsOwnerDto(UserEntity user);

    UserDataDto userEntityToUserDataDto(UserEntity userEntity);

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
}
