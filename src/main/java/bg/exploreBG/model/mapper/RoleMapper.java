package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.role.RoleDto;
import bg.exploreBG.model.entity.RoleEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    List<RoleDto> roleEntityToRoleDto (List<RoleEntity> roles);
}
