package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.SuitableForEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class SuitableForEnumSerializer extends JsonSerializer<List<SuitableForEnum>> {

    @Override
    public void serialize(
            List<SuitableForEnum> value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeStartArray();
        for (SuitableForEnum suitableForEnum : value) {
            gen.writeString(suitableForEnum.getValue());
        }
        gen.writeEndArray();
    }
}
