package bg.exploreBG.reviewable;

import bg.exploreBG.model.entity.ImageEntity;

import java.util.List;

public interface ReviewableWithImages extends ReviewableEntity {
    List<ImageEntity> getImages();

    Long getId();
}
