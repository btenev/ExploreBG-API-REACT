package bg.exploreBG.querybuilder;

import bg.exploreBG.repository.GpxRepository;
import org.springframework.stereotype.Component;

@Component
public class GpxQueryBuilder {
    private final GpxRepository gpxRepository;

    public GpxQueryBuilder(GpxRepository gpxRepository) {
        this.gpxRepository = gpxRepository;
    }

    public Long getReviewerIdByGpxId(Long gpxId) {
        return gpxRepository.getReviewerIdByGpxId(gpxId);
    }
}
