package bg.exploreBG.repository;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.custom.HikingTrailRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HikingTrailRepository extends JpaRepository<HikingTrailEntity, Long>, HikingTrailRepositoryCustom {

    @EntityGraph(attributePaths = {"likedByUsers"})
    Optional<HikingTrailEntity> findWithLikesByIdAndDetailsStatus(Long id, StatusEnum detailsStatus);

    Optional<HikingTrailEntity> findByIdAndCreatedBy_Email(Long id, String createdBy_email);

    /*used in deleteImages, we don't care about the status*/
    @EntityGraph(attributePaths = {"images"})
    Optional<HikingTrailEntity> findWithImagesByIdAndCreatedBy_Email(Long id, String createdBy_email);

    /*review trail superusers*/
    @EntityGraph(attributePaths = {"images"})
    Optional<HikingTrailEntity> findWithImagesByIdAndTrailStatus(
            Long id, SuperUserReviewStatusEnum trailStatus);

    Optional<HikingTrailEntity> findByIdAndDetailsStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used updateMainImage*/
    @EntityGraph(attributePaths = {"images"})
    Optional<HikingTrailEntity> findWithImagesByIdAndDetailsStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used in saveImages*/
    @EntityGraph(attributePaths = {"images", "createdBy"})
    Optional<HikingTrailEntity> findWithImagesAndImageReviewerByIdAndDetailsStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used in updateDestinations*/
    @EntityGraph(attributePaths = {"destinations"})
    Optional<HikingTrailEntity> findWithDestinationsByIdAndDetailsStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used in updateAvailableHuts*/
    @EntityGraph(attributePaths = {"availableHuts"})
    Optional<HikingTrailEntity> findWithHutsByIdAndDetailsStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            mi.imageUrl)
            FROM HikingTrailEntity t
            LEFT JOIN t.mainImage mi
            WHERE t.detailsStatus = :statusEnum
            """)
    Page<HikingTrailBasicDto> findAllByTrailStatus(@Param("statusEnum") StatusEnum statusEnum, Pageable pageable);

  /*  @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            mi.imageUrl,
               CASE
                   WHEN EXISTS (
                       SELECT 1
                       FROM t.likedByUsers lbu
                       WHERE lbu.email = :email)
                   THEN true
                   ELSE false
               END)
            FROM HikingTrailEntity t
            LEFT JOIN t.mainImage mi
            LEFT JOIN t.likedByUsers lbu
            WHERE t.trailStatus = :trailStatus
            ORDER BY
                CASE
                    WHEN :sortByLikedUser = true AND :sortDir = 'ASC' THEN CASE WHEN lbu.email = :email THEN 1 ELSE 0 END
                    WHEN :sortByLikedUser = true AND :sortDir = 'DESC' THEN CASE WHEN lbu.email = :email THEN 0 ELSE 1 END
                    ELSE t.id
                END
            """)
    Page<HikingTrailBasicLikesDto> findAllByTrailStatusWithUserLikes(
            @Param("trailStatus") StatusEnum statusEnum,
            @Param("email") String email,
            @Param("sortByLikedUser") Boolean sortByLikedUser,
            @Param("sortDir") String sortDir,
            Pageable pageable);*/
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
            AND t.detailsStatus = :approvedStatus

            UNION

            SELECT t
            FROM HikingTrailEntity t
            JOIN t.createdBy cb
            WHERE t.id = :id
            AND t.detailsStatus IN (:pendingStatus, :reviewStatus)
            AND cb.email = :email
              """)
    Optional<HikingTrailEntity> findByIdAndStatusApprovedOrStatusPendingAndOwner(
            @Param("id") Long id,
            @Param("email") String email,
            @Param("approvedStatus") StatusEnum approvedStatus,
            @Param("pendingStatus") StatusEnum pendingStatus,
            @Param("reviewStatus") StatusEnum reviewStatus
    );

    /*MultipleBagFetchException is use @EntityGraph with more than one list collection, use @Transactional for the time being*/
    Optional<HikingTrailEntity> findByIdAndDetailsStatus(Long id, StatusEnum detailsStatus);

    /*used in addNewTrailComment*/
    @EntityGraph(attributePaths = {"comments"})
    Optional<HikingTrailEntity> findWithCommentsByIdAndDetailsStatus(Long id, StatusEnum detailsStatus);

    @EntityGraph(attributePaths = {"comments"})
    Optional<HikingTrailEntity> findWithCommentsById(Long id);

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
    @EntityGraph(attributePaths = {"images", "images.reviewedBy"})
    Page<HikingTrailForApprovalProjection> getHikingTrailEntitiesByTrailStatus(SuperUserReviewStatusEnum status, Pageable pageable);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    int countHikingTrailEntitiesByTrailStatus(SuperUserReviewStatusEnum trailStatus);

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            mi.imageUrl)
            FROM HikingTrailEntity t
            LEFT JOIN t.mainImage mi
            WHERE t.detailsStatus = "APPROVED"
            ORDER BY function('RAND')
            """)
    List<HikingTrailBasicDto> findRandomApprovedTrails(Pageable pageable);

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            mi.imageUrl,
            CASE
               WHEN lbu.email = :email THEN true
               ELSE false
            END)
            FROM HikingTrailEntity t
            LEFT JOIN t.mainImage mi
            LEFT JOIN t.likedByUsers lbu ON lbu.email = :email
            WHERE t.detailsStatus = "APPROVED"
            ORDER BY function('RAND')
            """)
    List<HikingTrailBasicLikesDto> findRandomApprovedTrailsWithLikes(
            @Param("email") String email,
            Pageable pageable);

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailIdTrailNameDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint)
            )
            FROM HikingTrailEntity t
            WHERE t.detailsStatus = 'APPROVED'
            """)
    List<HikingTrailIdTrailNameDto> findAllBy();

    @Query("""
            SELECT r.id
            FROM HikingTrailEntity h
            JOIN h.reviewedBy r
            WHERE h.id = :trailId
            """)
    Long findReviewerId(@Param("trailId") Long trailId);

    @Override
    Page<HikingTrailBasicLikesDto> getTrailsWithLikes(StatusEnum statusEnum, String email, Pageable pageable, Boolean sortByLikedUser);
}
