package bg.exploreBG.utils;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;

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

    public static ImageEntity filterMainImage(List<ImageEntity> images, Long newMainImageId) {
        if (images == null) {
            throw new AppException("Image list cannot be null.", HttpStatus.BAD_REQUEST);
        }

        return images
                .stream()
                .filter(i -> i.getId().equals(newMainImageId))
                .findFirst()
                .orElseThrow(() ->
                        new AppException("Unable to update main image: The specified image is not part of the user's collection.",
                                HttpStatus.BAD_REQUEST));
    }

    public static List<Long> filterApprovedImageIds(List<ImageEntity> images, String email) {
        return images.stream().filter(image -> image.getStatus() == StatusEnum.REVIEW
                && image.getReviewedBy().getEmail().equals(email))
                .map(ImageEntity::getId)
                .toList();
    }
}
