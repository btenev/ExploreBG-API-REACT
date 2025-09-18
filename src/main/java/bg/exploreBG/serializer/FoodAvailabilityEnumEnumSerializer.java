package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.FoodAvailabilityEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class FoodAvailabilityEnumEnumSerializer extends JsonSerializer<FoodAvailabilityEnum> {
    @Override
    public void serialize(
            FoodAvailabilityEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
