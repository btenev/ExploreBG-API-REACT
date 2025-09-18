package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum FoodAvailabilityEnum {
    NO_INFORMATION("No information"),
    YES("Yes"),
    NO("No");

    private final String value;

    FoodAvailabilityEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static FoodAvailabilityEnum stringToWaterAvailabilityEnum(String value) {
        for (FoodAvailabilityEnum foodAvailabilityEnum : values()) {
            if (foodAvailabilityEnum.getValue().equals(value)) {
                return foodAvailabilityEnum;
            }
        }
        throw new AppException("Unknown enum food availability value: " + value, HttpStatus.BAD_REQUEST);
    }
}
