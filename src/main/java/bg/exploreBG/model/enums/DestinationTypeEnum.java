package bg.exploreBG.model.enums;

public enum DestinationTypeEnum {
    NATURAL_ATTRACTION("Natural attraction"),
    CULTURAL_HERITAGE("Cultural heritage");

    private final String value;
    DestinationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
