package bg.exploreBG.model.enums;

public enum SuperUserReviewStatusEnum {
    APPROVED("approved"),
    PENDING("pending");

    private final String value;

    SuperUserReviewStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
