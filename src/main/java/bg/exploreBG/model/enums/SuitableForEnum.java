package bg.exploreBG.model.enums;

public enum SuitableForEnum {
    HIKING("Hiking"),
    TRAIL_RUNNING("Trail running"),
    MOUNTAIN_BIKING("Mountain biking");

    private final String value;

    SuitableForEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
