package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record UserModRoleDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        Boolean moderator
) {
}
