package bg.exploreBG.reviewable;

import bg.exploreBG.model.entity.GpxEntity;

public interface ReviewableWithGpx extends ReviewableWithImages {
    GpxEntity getGpxFile();
}
