package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.AccommodationTypeEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccommodationTypeEnumSerializer extends JsonSerializer<AccommodationTypeEnum> {
    @Override
    public void serialize(
            AccommodationTypeEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
