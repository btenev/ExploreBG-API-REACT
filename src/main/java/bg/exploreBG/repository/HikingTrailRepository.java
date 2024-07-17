package bg.exploreBG.repository;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface HikingTrailRepository extends JpaRepository<HikingTrailEntity, Long> {

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            t.imageUrl
            )
            FROM HikingTrailEntity t WHERE t.id IN ?1
            """)
    List<HikingTrailBasicDto> findByIdIn(Set<Long> ids);


    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint)
            )
            FROM HikingTrailEntity t
            """)
    List<HikingTrailIdTrailNameDto> findAllBy();
}
