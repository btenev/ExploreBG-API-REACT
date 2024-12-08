package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.AccessibilityEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccessibilityEnumSerializer extends JsonSerializer<AccessibilityEnum> {
    @Override
    public void serialize(
            AccessibilityEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
