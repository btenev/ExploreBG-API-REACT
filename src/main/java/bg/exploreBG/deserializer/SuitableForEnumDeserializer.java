package bg.exploreBG.deserializer;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.enums.SuitableForEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//@JsonComponent()
public class SuitableForEnumDeserializer extends JsonDeserializer<List<SuitableForEnum>> {

    @Override
    public List<SuitableForEnum> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode tree = codec.readTree(p);

        if (tree == null || tree.isNull()  || tree.isEmpty()) {
            throw new AppException("Please enter at least one activity.", HttpStatus.BAD_REQUEST);
        }

        List<SuitableForEnum> result = new ArrayList<>();
        for (JsonNode jsonNode : tree) {
            String activity = jsonNode.asText();
            result.add(SuitableForEnum.stringToSuitableForEnum(activity));
        }

        return result;
    }

    @Override
    public List<SuitableForEnum> getNullValue(DeserializationContext ctxt) {
        throw new AppException("Please enter at least one activity.", HttpStatus.BAD_REQUEST);
    }
}
