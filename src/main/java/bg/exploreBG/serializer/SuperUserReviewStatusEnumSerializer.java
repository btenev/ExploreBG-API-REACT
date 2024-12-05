package bg.exploreBG.serializer;

import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class SuperUserReviewStatusEnumSerializer extends JsonSerializer<SuperUserReviewStatusEnum> {

    @Override
    public void serialize(
            SuperUserReviewStatusEnum value,
            JsonGenerator gen,
            SerializerProvider serializers
    ) throws IOException {
        gen.writeString(value.getValue());
    }
}
