package bg.exploreBG.model.dto.role;

import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.serializer.UserRoleEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record RoleDto(
        @JsonSerialize(using = UserRoleEnumSerializer.class)
        UserRoleEnum role
) {
}
