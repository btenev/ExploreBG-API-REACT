package bg.exploreBG.model.enums;

public enum GenderEnum {
    OTHER("Other"),
    MALE("Male"),
    FEMALE("Female");

    private final String value;

    GenderEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static GenderEnum stringToGenderEnum(String enumeration){
        return GenderEnum.valueOf(enumeration.toUpperCase());
    }
}
