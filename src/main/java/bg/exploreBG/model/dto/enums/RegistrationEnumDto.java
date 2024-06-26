package bg.exploreBG.model.dto.enums;

import bg.exploreBG.model.enums.GenderEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RegistrationEnumDto {
    public Set<String> gender;

    public RegistrationEnumDto() {
        this.gender = setGender();
    }

    private Set<String> setGender() {
        return Arrays.stream(GenderEnum.values())
                .map(GenderEnum::getValue)
                .collect(Collectors.toSet());
    }

    public Set<String> getGender() {
        return gender;
    }
}
