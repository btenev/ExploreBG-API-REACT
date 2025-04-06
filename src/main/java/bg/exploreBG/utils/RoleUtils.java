package bg.exploreBG.utils;

import bg.exploreBG.model.entity.UserEntity;

import java.util.List;
import java.util.stream.Collectors;

public class RoleUtils {
    public static List<String> getRoleNames(UserEntity user) {
        return user.getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toList());
    }
}
