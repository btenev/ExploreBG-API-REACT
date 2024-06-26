package bg.exploreBG.model.enums;

public enum WaterAvailabilityEnum {
    YES("Yes"),
    NO("No"),
    NO_INFORMATION("No information");

    private final String value;

    WaterAvailabilityEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
