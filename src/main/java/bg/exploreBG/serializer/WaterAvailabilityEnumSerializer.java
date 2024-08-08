package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.WaterAvailabilityEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WaterAvailabilityEnumSerializer extends JsonSerializer<WaterAvailabilityEnum> {
    @Override
    public void serialize(
            WaterAvailabilityEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
