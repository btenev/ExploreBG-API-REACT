package bg.exploreBG.model.enums;

public enum GenderEnum {
    OTHER("Other"),
    MALE("Male"),
    FEMALE("Female");

    private String value;

    GenderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
