package bg.exploreBG.repository;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface HikingTrailRepository extends JpaRepository<HikingTrailEntity, Long> {

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto(
            h.id,
            CONCAT(h.startPoint, ' - ', h.endPoint),
            h.trailInfo,
            h.imageUrl
            )
            FROM HikingTrailEntity h
            WHERE h.trailStatus = :statusEnum
            """)
    Page<HikingTrailBasicDto> findAllByTrailStatus(@Param("statusEnum")StatusEnum statusEnum, Pageable pageable);

    /*
   Investigate

       @Query("""
            SELECT t
            FROM HikingTrailEntity t
            JOIN t.createdBy AS cb
            WHERE t.id = :id
              AND (
                   t.trailStatus = 'APPROVED'
                   OR
                   (t.trailStatus = 'PENDING' AND cb.email = :email)
                   )
            """)
   */


    @Query("""
            SELECT t
            FROM HikingTrailEntity t
            WHERE t.id = :id
            AND t.trailStatus = 'APPROVED'

            UNION

            SELECT t
            FROM HikingTrailEntity t
            JOIN t.createdBy cb
            WHERE t.id = :id
            AND t.trailStatus IN (bg.exploreBG.model.enums.StatusEnum.PENDING, bg.exploreBG.model.enums.StatusEnum.REVIEW)
            AND cb.email = :email
              """)
    Optional<HikingTrailEntity> findByIdAndStatusApprovedOrStatusPendingAndOwner(@Param("id") Long id, @Param("email") String email);

    Optional<HikingTrailEntity> findByIdAndTrailStatus(Long id, StatusEnum trailStatus);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
//    @Query("""
//            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalDto(
//            t.id,
//            CONCAT(t.startPoint, ' - ', t.endPoint),
//            t.trailStatus,
//            t.creationDate,
//            new bg.exploreBG.model.dto.user.UserIdNameDto(rb.id, rb.username) as reviewedBy
//            )
//            FROM HikingTrailEntity t
//            JOIN t.reviewedBy rb
//            WHERE t.trailStatus in ?1
//            """)
    Page<HikingTrailForApprovalProjection> getHikingTrailEntitiesByTrailStatusIn(List<StatusEnum> trailStatus, Pageable pageable);

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
            WHERE t.id IN ?1 AND t.trailStatus = "APPROVED"
            """)
    List<HikingTrailBasicDto> findByIdIn(Set<Long> ids);


    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint)
            )
            FROM HikingTrailEntity t
            WHERE t.trailStatus = 'APPROVED'
            """)
    List<HikingTrailIdTrailNameDto> findAllBy();
}
