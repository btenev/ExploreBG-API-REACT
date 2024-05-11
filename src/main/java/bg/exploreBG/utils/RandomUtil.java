package bg.exploreBG.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomUtil {
    public static Set<Long> generateUniqueRandomIds(int limit, long countOfEntities) {
        Set<Long> randomIds = new HashSet<>();
        Random random = new Random();

        while (randomIds.size() < limit) {
            long randomId = Math.abs(random.nextLong() % countOfEntities);

            if (randomId == 0) {
                randomId = countOfEntities;
            }

            randomIds.add(randomId);
        }
        return randomIds;
    }
}
