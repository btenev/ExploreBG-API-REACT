package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum AccommodationTypeEnum {
    HUT("Hut"),
    GUEST_HOUSE("Guest house"),
    CAMPING("Camping"),
    SHELTER("Shelter");

    private final String value;

    AccommodationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AccommodationTypeEnum stringToAccommodationTypeEnum (String value) {
        for (AccommodationTypeEnum accommodationTypeEnum : values()) {
            if (accommodationTypeEnum.getValue().equals(value)) {
                return accommodationTypeEnum;
            }
        }
        throw new AppException("Unknown enum accommodation type value: " + value, HttpStatus.BAD_REQUEST);
    }
}
