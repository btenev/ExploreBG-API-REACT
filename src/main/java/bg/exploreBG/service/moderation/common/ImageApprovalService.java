package bg.exploreBG.service.moderation.common;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.reviewable.ReviewableWithImages;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImageApprovalService {

    public List<ImageEntity> approvalWithValidation(
            ReviewableWithImages entity,
            Set<Long> approvedImageIds,
            UserEntity reviewer
    ) {
        List<ImageEntity> images = entity.getImages();

        validateImageExistence(images);

        Set<ImageEntity> claimedImages = images.stream()
                .filter(image ->
                        image.getStatus() == StatusEnum.REVIEW &&
                                image.getReviewedBy() != null &&
                                image.getReviewedBy().equals(reviewer))
                .collect(Collectors.toSet());

        if (claimedImages.isEmpty()) {
            throw new AppException("You have not claimed any images for approval yet.", HttpStatus.BAD_REQUEST);
        }

        Set<Long> claimedImagesIds = claimedImages.stream()
                .map(ImageEntity::getId)
                .collect(Collectors.toSet());

        Set<Long> approvedIdsForVerification = new HashSet<>(approvedImageIds);

        if (!claimedImagesIds.equals(approvedImageIds)) {
            approvedIdsForVerification.removeAll(claimedImagesIds);

            if (!approvedIdsForVerification.isEmpty()) {
                throw new AppException("Image IDs: " + approvedIdsForVerification
                        + " are not part of the claimed images. Please revisit your approval IDs",
                        HttpStatus.BAD_REQUEST);
            }
        }

        claimedImages.forEach(image -> {
            if (approvedImageIds.contains(image.getId())) {
                image.setStatus(StatusEnum.APPROVED);
            }
        });

        return images;
    }

    private void validateImageExistence(List<ImageEntity> images) {
        if (images == null || images.isEmpty()) {
            throw new AppException("No images are available for this entity.", HttpStatus.BAD_REQUEST);
        }
    }
}
