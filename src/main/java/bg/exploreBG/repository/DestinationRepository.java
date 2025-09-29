package bg.exploreBG.repository;

import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicLikesDto;
import bg.exploreBG.model.dto.destination.DestinationForApprovalProjection;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.custom.DestinationRepositoryCustom;
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
public interface DestinationRepository extends JpaRepository<DestinationEntity, Long>, DestinationRepositoryCustom {

    @Query("""
            SELECT d.comments
            FROM DestinationEntity d
            WHERE d.id = :destinationId
            """)
    List<CommentEntity> findAllCommentsByDestinationId(@Param("destinationId") Long destinationId);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    int countDestinationEntitiesByEntityStatus(SuperUserReviewStatusEnum status);

    /*                             ---Not necessary.Projection does the job---
           @Query(" SELECT new bg.exploreBG.model.dto.destination.DestinationBasicDto(d.id, d.name, d.imageUrl, d.nextTo) " +
                  " FROM DestinationEntity d" +
                  " WHERE d.id IN ?1")
    */

    List<DestinationIdAndDestinationNameDto> findByStatus(StatusEnum destinationStatus);

    List<DestinationEntity> findByIdInAndStatus(List<Long> ids, StatusEnum statusEnum);

    @Transactional
    @Modifying
    @Query("""
            UPDATE DestinationEntity d
            SET d.createdBy.id = :newOwnerId
            WHERE d.createdBy.email = :oldOwnerEmail
            """)
    int removeUserFromDestinationsByEmail(
            @Param("newOwnerId") Long newOwnerId,
            @Param("oldOwnerEmail") String oldOwnerEmail);

    @Query("""
            SELECT new bg.exploreBG.model.dto.destination.DestinationBasicDto(
            d.id,
            d.destinationName,
            mi.imageUrl,
            d.nextTo)
            FROM DestinationEntity d
            LEFT JOIN d.mainImage mi ON mi.status = 'APPROVED'
            WHERE d.status = 'APPROVED'
            ORDER BY function('RAND')
            """)
    List<DestinationBasicDto> findRandomApprovedDestinations(Pageable pageable);

    /*   @Query("""
            SELECT new bg.exploreBG.model.dto.accommodation.AccommodationBasicDto(
            a.id,
            a.accommodationName,
            mi.imageUrl,
            a.nextTo)
            FROM AccommodationEntity a
            LEFT JOIN a.mainImage mi ON mi.status = 'APPROVED'
            WHERE a.status = 'APPROVED'
            ORDER BY function('RAND')
            """)
    List<AccommodationBasicDto> findRandomApprovedAccommodations(Pageable pageable);*/
    @Query("""
            SELECT new bg.exploreBG.model.dto.destination.DestinationBasicLikesDto(
            d.id,
            d.destinationName,
            mi.imageUrl,
            d.nextTo,
            CASE
                WHEN lbu.email = :email THEN true
                ELSE false
            END)
            FROM DestinationEntity d
            LEFT JOIN d.mainImage mi ON mi.status = 'APPROVED'
            LEFT JOIN d.likedByUsers lbu ON lbu.email = :email
            WHERE d.status = 'APPROVED'
            ORDER BY function('RAND')
            """)
    List<DestinationBasicLikesDto> findRandomApprovedDestinationsWithLikes(
            @Param("email") String email,
            Pageable pageable);

    @Override
    Page<DestinationBasicLikesDto> getDestinationsWithLikes(
            StatusEnum detailStatus,
            StatusEnum imageStatus,
            String email,
            Pageable pageable,
            Boolean sortByLikedUser);

    @Query("""
                   SELECT new bg.exploreBG.model.dto.destination.DestinationBasicDto(
                      d.id,
                      d.destinationName,
                      mi.imageUrl,
                      d.nextTo)
                   FROM DestinationEntity d
                   LEFT JOIN d.mainImage mi ON mi.status = 'APPROVED'
                   WHERE d.status = :status
            """)
    Page<DestinationBasicDto> findAllByStatus(@Param("status") StatusEnum status, Pageable pageable);

    @EntityGraph(attributePaths = {"likedByUsers"})
    Optional<DestinationEntity> findWithLikesByIdAndStatus(Long destinationId, StatusEnum status);

    @EntityGraph(attributePaths = {"images", "createdBy"})
    Optional<DestinationEntity> findWithImagesAndImageCreatorByIdAndStatusInAndCreatedBy_Email(
            Long destinationId,
            List<StatusEnum> statuses,
            String username
    );

    @EntityGraph(attributePaths = {"comments"})
    Optional<DestinationEntity> findWithCommentsByIdAndStatus(Long destinationId, StatusEnum status);

    @EntityGraph(attributePaths = {"comments"})
    Optional<DestinationEntity> findWithCommentsById(Long destinationId);

    Optional<DestinationEntity> findByIdAndStatusInAndCreatedBy_Email(
            Long destinationId, List<StatusEnum> detailStatus, String email);

    @EntityGraph(attributePaths = {"images"})
    Optional<DestinationEntity> findWithIMagesByIdAndStatusInAndCreatedBy_Email(
            Long destinationId,
            List<StatusEnum> statuses,
            String email);

    @EntityGraph(attributePaths = {"images", "images.reviewedBy", "reviewedBy"})
    Page<DestinationForApprovalProjection> getDestinationsEntitiesByEntityStatus(
            SuperUserReviewStatusEnum status,
            Pageable pageable);

    Optional<DestinationEntity> findByIdAndEntityStatus(Long destinationId, SuperUserReviewStatusEnum supeStatus);

    @EntityGraph(attributePaths = {"images", "images.reviewedBy"})
    Optional<DestinationEntity> findWithImagesAndImageReviewerById(Long destinationId);

    @EntityGraph(attributePaths = {"images"})
    Optional<DestinationEntity> findByIdAndCreatedBy_Email(Long destinationId, String email);

    Optional<DestinationEntity> findByIdAndStatus(Long destinationId, StatusEnum detailsStatus);
}
