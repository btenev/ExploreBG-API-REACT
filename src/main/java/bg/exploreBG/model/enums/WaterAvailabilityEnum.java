package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum WaterAvailabilityEnum {

    NO_INFORMATION("No information"),
    YES("Yes"),
    NO("No");

    private final String value;

    WaterAvailabilityEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static WaterAvailabilityEnum stringToWaterAvailabilityEnum(String value) {
        for (WaterAvailabilityEnum waterAvailabilityEnum : values()) {
            if (waterAvailabilityEnum.getValue().equals(value)) {
                return waterAvailabilityEnum;
            }
        }
        throw new AppException("Unknown enum water availability value: " + value, HttpStatus.BAD_REQUEST);
    }
}
