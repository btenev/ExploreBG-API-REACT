package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.DifficultyLevelEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DifficultyLevelEnumSerializer extends JsonSerializer<DifficultyLevelEnum> {
    @Override
    public void serialize(
            DifficultyLevelEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeNumber(value.getLevel());
    }
}
