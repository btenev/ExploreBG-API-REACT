package bg.exploreBG.model.enums;

public enum UserRoleEnum {
    MEMBER("Member"),
    MODERATOR("Moderator"),
    ADMIN("Admin");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
