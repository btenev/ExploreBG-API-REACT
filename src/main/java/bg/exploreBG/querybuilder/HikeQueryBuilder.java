package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.repository.HikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class HikeQueryBuilder {
    private final HikeRepository repository;

    public HikeQueryBuilder(HikeRepository repository) {
        this.repository = repository;
    }

    public long getHikeCount() {
        return repository.count();
    }

    public List<HikeEntity> getHikeEntitiesByIds(Set<Long> hikeIds) {
        return this.repository.findByIdIn(hikeIds);
    }

    public HikeEntity getHikeEntityById(Long hikeId) {
        return this.repository.findById(hikeId).orElseThrow(this::hikeNotFoundException);
    }

    public Page<HikeEntity> getAllHikeEntities(Pageable pageable) {
        return this.repository.findAll(pageable);
    }

    public void removeHikingTrailFromHikesByTrailIdIfTrailOwner(Long trailId, String email) {
        int rows = this.repository.removeHikingTrailFromHikesByHikingTrailIdIfTrailOwner(trailId, email);
        if (rows == 0) {
            throw trailNotFoundOrNotOwnerException();
        }
    }

    private AppException hikeNotFoundException() {
        return new AppException("The hike you are looking for was not found.", HttpStatus.NOT_FOUND);
    }

    private AppException trailNotFoundOrNotOwnerException() {
        return new AppException("The hiking trail you are looking for was not found or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }
}
