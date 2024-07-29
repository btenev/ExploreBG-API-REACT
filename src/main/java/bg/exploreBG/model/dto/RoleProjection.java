package bg.exploreBG.model.dto;

import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.serializer.UserRoleEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public interface RoleProjection {
    @JsonSerialize(using = UserRoleEnumSerializer.class)
    UserRoleEnum getRole();
}
