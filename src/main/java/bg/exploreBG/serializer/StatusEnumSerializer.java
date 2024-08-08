package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.StatusEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StatusEnumSerializer extends JsonSerializer<StatusEnum> {
    @Override
    public void serialize(
            StatusEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
