package bg.exploreBG.model.dto.enums;

import bg.exploreBG.model.enums.DifficultyLevelEnum;
import bg.exploreBG.model.enums.SeasonEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.enums.WaterAvailabilityEnum;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CreateHikingTrailEnumDto {
    private final Set<String> seasonVisited;
    private final Set<String> waterAvailability;
    private final int[] trailDifficulty;
    private final Set<String> activity;

    public CreateHikingTrailEnumDto() {
        this.seasonVisited = setSeasonVisited();
        this.waterAvailability = setWaterAvailability();
        this.trailDifficulty = setTrailDifficulty();
        this.activity = setSuitableFor();
    }

    private Set<String> setSeasonVisited() {
        return Arrays.stream(SeasonEnum.values())
                .map(SeasonEnum::getValue)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<String> setWaterAvailability() {
        return Arrays.stream(WaterAvailabilityEnum.values())
                .map(WaterAvailabilityEnum::getValue)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private int[] setTrailDifficulty() {
        return Arrays.stream(DifficultyLevelEnum.values())
                .mapToInt(DifficultyLevelEnum::getLevel)
                .toArray();

    }

    private Set<String> setSuitableFor() {
        return Arrays.stream(SuitableForEnum.values())
                .map(SuitableForEnum::getValue)
                .collect(Collectors.toSet());
    }

    public Set<String> getSeasonVisited() {
        return seasonVisited;
    }

    public Set<String> getWaterAvailability() {
        return waterAvailability;
    }

    public int[] getTrailDifficulty() {
        return trailDifficulty;
    }

    public Set<String> getActivity() {
        return activity;
    }
}
