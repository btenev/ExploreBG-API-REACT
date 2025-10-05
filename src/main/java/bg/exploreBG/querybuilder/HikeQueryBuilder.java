package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.repository.HikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class HikeQueryBuilder {
    private final HikeRepository repository;

    private final Logger logger = LoggerFactory.getLogger(HikeQueryBuilder.class);

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

    public HikeEntity getHikeWithCommentsById(Long hikeId) {
        return this.repository.findWithCommentsById(hikeId).orElseThrow(this::hikeNotFoundException);
    }
    public HikeEntity getHikeByIdIfOwner(Long hikeId, String email) {
        return this.repository.findByIdAndCreatedBy_Email(hikeId, email)
                .orElseThrow(this::hikeNotFoundOrNotOwnerException);
    }

    public void removeHikingTrailFromHikesByTrailIdIfTrailOwner(Long trailId, String email) {
        int rows = this.repository.removeHikingTrailFromHikesByHikingTrailIdIfTrailOwner(trailId, email);
        if (rows == 0) {
            this.logger.info(
                    "Query executed successfully, but no hikes were updated. This likely means the trail (ID: {}) is not associated with any hikes or the user (email: {}) is not the owner.",
                    trailId, email
            );
        } else {
            this.logger.info("Successfully removed trail (ID: {}) from {} hike(s) for user (email: {}).", trailId, rows, email);
        }
    }

    public void removeUserFromHikesByEmail(Long newOwnerId, String oldOwnerEmail) {
        int row = this.repository.removeUserFromHikesByEmail(newOwnerId, oldOwnerEmail);
        if (row == 0) {
            this.logger.warn("No hikes updated for user email: {}", oldOwnerEmail);
        }
    }

    private AppException hikeNotFoundException() {
        return new AppException("The hike you are looking for was not found.", HttpStatus.NOT_FOUND);
    }

    private AppException hikeNotFoundOrNotOwnerException() {
        return new AppException("The hike you are looking for was not found or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }
}
