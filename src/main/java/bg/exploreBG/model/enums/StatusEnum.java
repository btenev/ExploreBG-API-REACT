package bg.exploreBG.model.enums;

public enum StatusEnum {
    APPROVED("approved"),
    PENDING("pending"),
    REVIEW("review");

    private String value;

    StatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
