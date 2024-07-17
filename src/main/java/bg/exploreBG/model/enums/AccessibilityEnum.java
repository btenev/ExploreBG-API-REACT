package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum AccessibilityEnum {
    ON_FOOT("On foot"),
    BY_CAR("By car");
    private final String value;

    AccessibilityEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AccessibilityEnum stringToAccessibilityEnum (String value) {
        for (AccessibilityEnum accessibilityEnum : values()) {
            if (accessibilityEnum.getValue().equals(value)) {
                return accessibilityEnum;
            }
        }
        throw new AppException("Unknown enum accessibility value: " + value, HttpStatus.BAD_REQUEST);
    }
}
