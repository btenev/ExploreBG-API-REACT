package bg.exploreBG.repository;

import bg.exploreBG.model.dto.hikingTrail.*;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.custom.HikingTrailRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface HikingTrailRepository extends JpaRepository<HikingTrailEntity, Long>, HikingTrailRepositoryCustom {

    @EntityGraph(attributePaths = {"likedByUsers"})
    Optional<HikingTrailEntity> findWithLikesByIdAndStatus(Long id, StatusEnum detailsStatus);

    Optional<HikingTrailEntity> findByIdAndCreatedBy_Email(Long id, String createdBy_email);

    @EntityGraph(attributePaths = {"images", "images.reviewedBy"})
    Optional<HikingTrailEntity> findWithImagesById(Long trailId);

    /*used in deleteImages, we don't care about the status*/
    @EntityGraph(attributePaths = {"images"})
    Optional<HikingTrailEntity> findWithImagesByIdAndCreatedBy_Email(Long id, String createdBy_email);

    Optional<HikingTrailEntity> findByIdAndStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used updateMainImage*/
    @EntityGraph(attributePaths = {"images"})
    Optional<HikingTrailEntity> findWithImagesByIdAndStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used in saveImages*/
    @EntityGraph(attributePaths = {"images", "createdBy"})
    Optional<HikingTrailEntity> findWithImagesAndImageCreatorByIdAndStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used in updateDestinations*/
    @EntityGraph(attributePaths = {"destinations"})
    Optional<HikingTrailEntity> findWithDestinationsByIdAndStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    /*used in updateAvailableHuts*/
    @EntityGraph(attributePaths = {"availableHuts"})
    Optional<HikingTrailEntity> findWithHutsByIdAndStatusInAndCreatedByEmail(
            Long id, List<StatusEnum> detailsStatus, String createdBy_email);

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            mi.imageUrl)
            FROM HikingTrailEntity t
            LEFT JOIN t.mainImage mi
            WHERE t.status = :statusEnum
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

/*    @Query("""
            SELECT t
            FROM HikingTrailEntity t
            WHERE t.id = :id
            AND t.status = :approvedStatus

            UNION

            SELECT t
            FROM HikingTrailEntity t
            JOIN t.createdBy cb
            WHERE t.id = :id
            AND t.status IN (:pendingStatus, :reviewStatus)
            AND cb.email = :email
            """)
    Optional<HikingTrailEntity> findByIdAndStatusApprovedOrStatusPendingAndOwner(
            @Param("id") Long id,
            @Param("email") String email,
            @Param("approvedStatus") StatusEnum approvedStatus,
            @Param("pendingStatus") StatusEnum pendingStatus,
            @Param("reviewStatus") StatusEnum reviewStatus
    );*/

    /*MultipleBagFetchException is use @EntityGraph with more than one list collection, use @Transactional for the time being*/
    /*@EntityGraph(attributePaths = {"images"})*/
    Optional<HikingTrailEntity> findByIdAndStatus(Long id, StatusEnum detailsStatus);

    /*used in addNewTrailComment*/
    @EntityGraph(attributePaths = {"comments"})
    Optional<HikingTrailEntity> findWithCommentsByIdAndStatus(Long id, StatusEnum detailsStatus);

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
    @EntityGraph(attributePaths = {"images", "images.reviewedBy", "gpxFile", "gpxFile.reviewedBy", "reviewedBy"})
    Page<HikingTrailForApprovalProjection> getHikingTrailEntitiesByEntityStatus(SuperUserReviewStatusEnum status, Pageable pageable);

    @EntityGraph(attributePaths = {"images", "images.reviewedBy", "gpxFile"})
    Optional<HikingTrailEntity> findWithImageAndGpxFileById(Long id);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    int countHikingTrailEntitiesByEntityStatus(SuperUserReviewStatusEnum trailStatus);

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto(
            t.id,
            CONCAT(t.startPoint, ' - ', t.endPoint),
            t.trailInfo,
            mi.imageUrl)
            FROM HikingTrailEntity t
            LEFT JOIN t.mainImage mi
            WHERE t.status = "APPROVED"
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
            WHERE t.status = "APPROVED"
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
            WHERE t.status = 'APPROVED'
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

    @Query("""
            SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailImageStatusAndGpxFileStatus(
              CASE
                   WHEN COUNT(i) = 0 THEN null
                   WHEN COUNT(i) > 0
                   AND SUM(CASE WHEN i.status = 'APPROVED' THEN 1 ELSE 0 END) = COUNT(i)
                   THEN 'APPROVED'
                   ELSE 'NOT_APPROVED'
              END,
              f.status
            )
            FROM HikingTrailEntity t
            LEFT JOIN t.images i
            LEFT JOIN t.gpxFile f
            WHERE t.id = :trailId
            GROUP BY f.status
            """)
    Optional<HikingTrailImageStatusAndGpxFileStatus> findImageStatusAndGpxStatusById(@Param("trailId") Long id);

    @EntityGraph(attributePaths = {"gpxFile", "gpxFile.reviewedBy"})
    Optional<HikingTrailEntity> findWithGpxFileAndReviewerById(Long id);

    @EntityGraph(attributePaths = {"hikes"})
    Optional<HikingTrailEntity> findWithHikesHikingTrailByIdAndCreatedByEmail(Long id, String createdBy_email);

    void deleteByIdAndCreatedBy_Email(Long id, String createdBy_email);

    @Transactional
    @Modifying
    @Query("""
            UPDATE HikingTrailEntity t
            SET t.createdBy.id = :newOwnerId
            WHERE t.createdBy.email = :oldOwnerEmail
            """)
    int removeUserEntityFromHikingTrailByEmail(
            @Param("newOwnerId") Long newOwnerId,
            @Param("oldOwnerEmail") String oldOwnerEmail);

    /*review trail superusers*/
    Optional<HikingTrailEntity> findByIdAndEntityStatus(Long id, SuperUserReviewStatusEnum trailStatus);
}
