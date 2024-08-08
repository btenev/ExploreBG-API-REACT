package bg.exploreBG.repository;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HikingTrailRepository extends JpaRepository<HikingTrailEntity, Long> {

    Optional<HikingTrailEntity> findByIdAndTrailStatus(Long id, StatusEnum trailStatus);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailStatus,
            t.creationDate,
            u.username
            )
            FROM HikingTrailEntity t
            LEFT JOIN t.createdBy as u
            WHERE t.trailStatus in ?1
            """)
    Page<HikingTrailForApprovalDto> getHikingTrailEntitiesByTrailStatus(StatusEnum trailStatus, Pageable pageable);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    int countHikingTrailEntitiesByTrailStatus(StatusEnum status);

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            t.imageUrl
            )
            FROM HikingTrailEntity t
            WHERE t.id IN ?1
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
