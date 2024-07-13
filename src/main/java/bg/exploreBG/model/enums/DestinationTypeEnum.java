package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum DestinationTypeEnum {
    NATURAL_ATTRACTION("Natural attraction"),
    CULTURAL_HERITAGE("Cultural heritage");

    private final String value;
    DestinationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DestinationTypeEnum  stringToDestinationTypeEnum(String value) {
        for (DestinationTypeEnum destinationTypeEnum : values()) {
            if (destinationTypeEnum.getValue().equals(value)) {
                return destinationTypeEnum;
            }
        }
        throw new AppException("Unknown enum destination type value: " + value, HttpStatus.BAD_REQUEST);
    }
}
