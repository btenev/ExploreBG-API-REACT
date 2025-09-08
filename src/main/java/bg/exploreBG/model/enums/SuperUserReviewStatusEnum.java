package bg.exploreBG.model.enums;

public enum SuperUserReviewStatusEnum {
    APPROVED("Approved"),
    PENDING("Pending");

    private final String value;

    SuperUserReviewStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
