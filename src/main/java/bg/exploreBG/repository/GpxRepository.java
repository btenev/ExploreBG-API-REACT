package bg.exploreBG.repository;

import bg.exploreBG.model.entity.GpxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GpxRepository extends JpaRepository<GpxEntity, Long> {

    @Query("""
            SELECT r.id
            FROM GpxEntity gpx
            JOIN gpx.reviewedBy r
            WHERE gpx.Id = :gpxId
            """)
    Long getReviewerIdByGpxId(@Param("gpxId") Long gpxId);
}
