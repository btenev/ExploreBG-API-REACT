package bg.exploreBG.utils;

import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.reviewable.ReviewableWithImages;

import java.util.List;
import java.util.function.Function;

public class PublicEntityUtils {

    @FunctionalInterface
    public interface EntityFetcher<E> {
        E fetch(Long id, StatusEnum status);
    }

    private PublicEntityUtils() {}

    public static <E extends ReviewableWithImages, D> D fetchAndMapWithApprovedImages (
            Long id,
            StatusEnum detailsStatus,
            EntityFetcher<E> fetcher,
            Function<E, D> mapper
    ) {
        E entity = fetcher.fetch(id, detailsStatus);

        List<ImageEntity> approvedImages = ImageUtils.filterByStatus(entity.getImages(), detailsStatus);

        if (approvedImages.size() != entity.getImages().size()) {
            entity.setImages(approvedImages);
        }

        return mapper.apply(entity);
    }
}
