package bg.exploreBG.utils;

import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.StatusEnum;

import java.util.Collections;
import java.util.List;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static List<ImageEntity> filterByStatus(List<ImageEntity> images, StatusEnum status) {
        if (images == null) {
            return Collections.emptyList();
        }

        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }

        return images.stream().filter(image -> status.equals(image.getStatus())).toList();
    }
}
