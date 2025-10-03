package bg.exploreBG.interfaces;

import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;

import java.util.List;

public interface ReviewableWithImages extends ReviewableEntity {
    Long getId();

    List<ImageEntity> getImages();

    void setImages(List<ImageEntity> images);

    ImageEntity getMainImage();

    void setMainImage(ImageEntity image);

    int getMaxNumberOfImages();

    SuperUserReviewStatusEnum getEntityStatus();

    void setEntityStatus(SuperUserReviewStatusEnum status);
}
