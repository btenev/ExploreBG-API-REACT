package bg.exploreBG.interfaces;

import bg.exploreBG.model.entity.ImageEntity;

import java.util.List;

public interface HasMainImage {
    List<ImageEntity> getImages();
    ImageEntity getMainImage();
    void setMainImage(ImageEntity mainImage);
}
