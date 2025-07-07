package bg.exploreBG.utils;

import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.UserRoleEnum;

import java.util.List;
import java.util.stream.Collectors;

public class RoleUtils {
    public static List<String> getRoleNames(UserEntity user) {
        return user.getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toList());
    }

    public static List<UserRoleEnum>  getUserRoles(UserEntity user) {
        return user.getRoles()
                .stream()
                .map(RoleEntity::getRole)
                .collect(Collectors.toList());
    }
}
