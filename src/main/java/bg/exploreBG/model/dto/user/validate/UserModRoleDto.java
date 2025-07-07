package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;

public record UserModRoleDto(
        @NotNull(message = "Please specify whether the account role should be set to moderator.")
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        Boolean moderator
) {
}
