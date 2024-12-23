package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.DestinationTypeEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DestinationTypeEnumSerializer extends JsonSerializer<DestinationTypeEnum> {
    @Override
    public void serialize(
            DestinationTypeEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
