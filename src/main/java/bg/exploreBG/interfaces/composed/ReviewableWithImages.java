package bg.exploreBG.interfaces.composed;

import bg.exploreBG.interfaces.base.HasEntityStatus;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;

import java.util.List;

public interface ReviewableWithImages extends ReviewableEntity, HasMainImage, HasEntityStatus {
    Long getId();
    void setImages(List<ImageEntity> images);
    int getMaxNumberOfImages();
    SuperUserReviewStatusEnum getEntityStatus();
}
