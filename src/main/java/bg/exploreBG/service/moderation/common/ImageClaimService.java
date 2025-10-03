package bg.exploreBG.service.moderation.common;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.interfaces.ReviewableWithImages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ImageClaimService {

    public List<ImageEntity> toggleImageClaim(
            ReviewableWithImages entity,
            Boolean claimImage,
            UserEntity reviewer
    ) {
        List<ImageEntity> images = entity.getImages();
        validateImageExistence(images);

        if (claimImage) {
            handleClaimImages(images, reviewer);
        } else {
            cancelClaimImages(images, reviewer);
        }

        return images;
    }

    private void handleClaimImages(List<ImageEntity> images, UserEntity reviewer) {
        Set<ImageEntity> pending = filterImages(images, image -> image.getStatus() == StatusEnum.PENDING);

        Set<ImageEntity> reviewed =
                filterImages(images, image -> image.getReviewedBy() != null && image.getReviewedBy().equals(reviewer));

        if (pending.isEmpty()) {
            if (reviewed.isEmpty()) {
                throw new AppException("No images with suitable status available for review.", HttpStatus.BAD_REQUEST);
            } else {
                throw new AppException("You have already claimed those images for review!", HttpStatus.BAD_REQUEST);
            }
        } else {
            images.forEach(image -> {
                if (pending.contains(image)) {
                    image.setReviewedBy(reviewer);
                    image.setStatus(StatusEnum.REVIEW);
                };
            });
        }
    }

    private void cancelClaimImages(List<ImageEntity> images, UserEntity reviewer) {
        Set<ImageEntity> filtered =
                filterImages(images,
                        image -> image.getStatus() == StatusEnum.REVIEW
                                && (image.getReviewedBy() != null && image.getReviewedBy().equals(reviewer)));

        if (filtered.isEmpty()) {
            throw new AppException("You can not cancel images that you haven't claimed.", HttpStatus.BAD_REQUEST);
        }

        images.forEach(image -> {
            if (filtered.contains(image)) {
                image.setReviewedBy(null);
                image.setStatus(StatusEnum.PENDING);
            }
        });
    }

    private Set<ImageEntity> filterImages(List<ImageEntity> images, Predicate<ImageEntity> filter) {
        return images.stream().filter(filter).collect(Collectors.toSet());
    }

    private void validateImageExistence(List<ImageEntity> images) {
        if (images == null || images.isEmpty()) {
            throw new AppException("No images are available for this entity.", HttpStatus.BAD_REQUEST);
        }
    }
}
