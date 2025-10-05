package bg.exploreBG.interfaces.composed;

import bg.exploreBG.model.entity.GpxEntity;

public interface ReviewableWithGpx extends ReviewableWithImages {
    GpxEntity getGpxFile();
}
