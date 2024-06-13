package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.user.UserDetailsDto;
import bg.exploreBG.model.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDetailsDto userEntityToUserDetailsDto(UserEntity user);

}
