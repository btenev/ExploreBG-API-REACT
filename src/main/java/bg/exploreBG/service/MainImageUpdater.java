package bg.exploreBG.service;

import bg.exploreBG.interfaces.composed.HasMainImage;
import bg.exploreBG.model.dto.image.validate.ImageMainUpdateDto;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.utils.EntityUpdateUtils;
import bg.exploreBG.utils.ImageUtils;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class MainImageUpdater {

    public <E extends HasMainImage> long updateMainImage(
            E entity,
            ImageMainUpdateDto dto,
            Consumer<E> saver
    ) {
        ImageEntity found = ImageUtils.filterMainImage(entity.getImages(), dto.imageId());

        boolean isUpdated =
                EntityUpdateUtils.updateFieldIfDifferent(entity::getMainImage, entity::setMainImage, found);

        if (isUpdated) {
            saver.accept(entity);
        }

        return found.getId();
    }
}
