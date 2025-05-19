package bg.exploreBG.model.enums;

public enum StatusEnum {
    APPROVED("Approved"),
    PENDING("Pending"),
    REVIEW("Review");

    private final String value;

    StatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
