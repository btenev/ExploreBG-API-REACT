package bg.exploreBG.repository;

import bg.exploreBG.model.entity.HikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface HikeRepository extends JpaRepository<HikeEntity, Long> {
    List<HikeEntity> findByIdIn(Set<Long> ids);

    @Transactional
    @Modifying
    @Query("""
            UPDATE HikeEntity h
            SET h.hikingTrail = null
            WHERE h.hikingTrail.id = :trailId
                AND EXISTS (
                       SELECT 1
                       FROM HikingTrailEntity ht
                       JOIN ht.createdBy cb
                       WHERE  cb.email = :ownerEmail)
            """)
    int removeHikingTrailFromHikesByHikingTrailIdIfTrailOwner(
            @Param("trailId") Long trailId,
            @Param("ownerEmail") String ownerEmail
    );
}
