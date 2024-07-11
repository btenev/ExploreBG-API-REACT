package bg.exploreBG.deserializer;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.enums.SuitableForEnum;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.jackson.JsonComponent;
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

        if (tree == null || tree.isEmpty()) {
            throw new AppException("Invalid enum season value", HttpStatus.BAD_REQUEST);
        }

        List<SuitableForEnum> result = new ArrayList<>();
        for (JsonNode jsonNode : tree) {
            String activity = jsonNode.asText();
            result.add(SuitableForEnum.stringToSuitableForEnum(activity));
        }

        return result;
    }
}
