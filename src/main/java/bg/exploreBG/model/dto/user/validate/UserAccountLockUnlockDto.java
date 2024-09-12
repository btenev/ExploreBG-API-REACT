package bg.exploreBG.model.dto.user.validate;

import bg.exploreBG.deserializer.StrictBooleanDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public record UserAccountLockUnlockDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        Boolean lockAccount
) {
}
