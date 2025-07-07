package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;

public record UserAccountLockRequestDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        @NotNull(message = "Please specify whether the account should be locked.")
        Boolean lockAccount
) {
}
