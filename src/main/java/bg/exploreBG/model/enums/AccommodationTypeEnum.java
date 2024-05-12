package bg.exploreBG.model.enums;

public enum AccommodationTypeEnum {
    HUT("Hut"),
    GUEST_HOUSE("Guest house"),
    CAMPING("Camping"),
    SHELTER("Shelter");

    private final String value;

    AccommodationTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
