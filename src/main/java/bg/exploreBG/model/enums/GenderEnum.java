package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum GenderEnum {
    OTHER("Other"),
    MALE("Male"),
    FEMALE("Female");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static GenderEnum stringToGenderEnum(String value) {
        for (GenderEnum genderEnum : values()) {
            if (genderEnum.getValue().equals(value)) {
                return genderEnum;
            }
        }
        throw new AppException("Unknown enum gender value: " + value, HttpStatus.BAD_REQUEST);
    }
}
