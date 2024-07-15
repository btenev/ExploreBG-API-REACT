package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum SeasonEnum {
    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall"),
    WINTER("Winter");

    private final String value;

    SeasonEnum (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SeasonEnum stringToSeasonEnum(String value) {
        for (SeasonEnum seasonEnum : values()) {
            if (seasonEnum.getValue().equals(value)) {
                return seasonEnum;
            }
        }
        throw new AppException("Unknown enum season value: " + value, HttpStatus.BAD_REQUEST);
    }
}
