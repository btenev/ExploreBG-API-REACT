package bg.exploreBG.model.enums;

public enum SeasonEnum {
    SUMMER("Summer"),
    FALL("Fall"),
    WINTER("Winter"),
    SPRING("Spring");

    private final String value;

    SeasonEnum (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
