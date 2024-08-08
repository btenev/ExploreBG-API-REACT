package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.SeasonEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SeasonEnumSerializer extends JsonSerializer<SeasonEnum> {
    @Override
    public void serialize(
            SeasonEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
