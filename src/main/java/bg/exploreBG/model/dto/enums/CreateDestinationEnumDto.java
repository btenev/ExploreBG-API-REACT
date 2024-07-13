package bg.exploreBG.model.dto.enums;

import bg.exploreBG.model.enums.DestinationTypeEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateDestinationEnumDto {
    private final Set<String> type;

    public CreateDestinationEnumDto() {
        this.type = setType();
    }

    private Set<String> setType() {
        return Arrays.stream(DestinationTypeEnum.values())
                .map(DestinationTypeEnum::getValue)
                .collect(Collectors.toSet());
    }

    public Set<String> getType() {
        return type;
    }
}
