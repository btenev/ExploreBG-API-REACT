package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import org.springframework.http.HttpStatus;

public enum SuitableForEnum {
    HIKING("hiking"),
    TRAIL_RUNNING("trail-running"),
    MOUNTAIN_BIKING("mountain-biking");

    private final String value;

    SuitableForEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SuitableForEnum stringToSuitableForEnum(String value) {
        for (SuitableForEnum suitableForEnum : values()) {
            if (suitableForEnum.getValue().equals(value)) {
                return suitableForEnum;
            }
        }
        throw new AppException("Unknown enum suitable for value: " + value, HttpStatus.BAD_REQUEST);
    }
}
