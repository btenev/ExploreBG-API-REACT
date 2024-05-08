package bg.exploreBG.model.enums;

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
}
