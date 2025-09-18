package bg.exploreBG.model.dto.enums;

import bg.exploreBG.model.enums.AccessibilityEnum;
import bg.exploreBG.model.enums.AccommodationTypeEnum;
import bg.exploreBG.model.enums.FoodAvailabilityEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateAccommodationEnumDto {
    private final Set<String> type;
    private final Set<String> access;
    private final Set<String> availableFood;

    public CreateAccommodationEnumDto() {
        this.availableFood = setAvailableFood();
        this.type = setType();
        this.access = setAccess();
    }

    private Set<String> setType() {
        return Arrays.stream(AccommodationTypeEnum.values())
                .map(AccommodationTypeEnum::getValue)
                .collect(Collectors.toSet());
    }

    private Set<String> setAccess() {
        return Arrays.stream(AccessibilityEnum.values())
                .map(AccessibilityEnum::getValue)
                .collect(Collectors.toSet());
    }

    private Set<String> setAvailableFood() {
        return Arrays.stream(FoodAvailabilityEnum.values())
                .map(FoodAvailabilityEnum::getValue)
                .collect(Collectors.toSet());
    }

    public Set<String> getType() {
        return type;
    }

    public Set<String> getAccess() {
        return access;
    }

    public Set<String> getAvailableFood() {
        return availableFood;
    }
}
