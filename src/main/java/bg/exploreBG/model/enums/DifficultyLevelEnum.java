package bg.exploreBG.model.enums;

import bg.exploreBG.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum DifficultyLevelEnum {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    private final int level;

    DifficultyLevelEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static DifficultyLevelEnum intToDifficultyLevelEnum(int value) {
        for (DifficultyLevelEnum levelEnum : values()) {
            if (levelEnum.getLevel() == value) {
                return levelEnum;
            }
        }
        throw new AppException("Unknown enum difficulty level value: " + value, HttpStatus.BAD_REQUEST);
    }
}
