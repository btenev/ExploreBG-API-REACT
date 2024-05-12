package bg.exploreBG.model.enums;

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
}
