package bg.exploreBG.repository;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicLikesDto;
import bg.exploreBG.model.dto.accommodation.AccommodationForApprovalProjection;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.custom.AccommodationRepositoryCustom;
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
public interface AccommodationRepository extends JpaRepository<AccommodationEntity, Long>, AccommodationRepositoryCustom {

    @Query("""
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
    List<AccommodationBasicDto> findRandomApprovedAccommodations(Pageable pageable);

    @Query("""
            SELECT new bg.exploreBG.model.dto.accommodation.AccommodationBasicLikesDto(
            a.id,
            a.accommodationName,
            mi.imageUrl,
            a.nextTo,
            CASE
                WHEN lbu.email = :email THEN true
                ELSE false
            END)
            FROM AccommodationEntity a
            LEFT JOIN a.mainImage mi ON mi.status = 'APPROVED'
            LEFT JOIN a.likedByUsers lbu ON lbu.email = :email
            WHERE a.status = 'APPROVED'
            ORDER BY function('RAND')
            """)
    List<AccommodationBasicLikesDto> findRandomApprovedAccommodationsWithLikes(
            @Param("email") String email,
            Pageable pageable);

    @Query("""
            SELECT new bg.exploreBG.model.dto.accommodation.AccommodationBasicDto(
            a.id,
            a.accommodationName,
            mi.imageUrl,
            a.nextTo)
            FROM AccommodationEntity a
            LEFT JOIN a.mainImage mi ON mi.status = 'APPROVED'
            WHERE a.status = :status
            """)
    Page<AccommodationBasicDto> findAllByStatus(@Param("status") StatusEnum status, Pageable pageable);

    @Transactional
    @Modifying
    @Query("""
            UPDATE AccommodationEntity a
            SET a.createdBy.id= :newOwnerId
            WHERE a.createdBy.email = :oldOwnerEmail
            """)
    int removeUserEntityFromAccommodationsByUserEntityEmailIfOwner(
            @Param("newOwnerId") Long newOwnerId,
            @Param("oldOwnerEmail") String oldOwnerEmail);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    int countAccommodationEntitiesByAccommodationStatus(SuperUserReviewStatusEnum superUserReviewStatus);

    /*
    @Query("""
       SELECT new bg.exploreBG.model.dto.accommodation.AccommodationBasicDto(a.id, a.accommodationName, a.imageUrl, a.nextTo)
       FROM AccommodationEntity a
       WHERE a.id IN ?1
    """)   
     */

    List<AccommodationIdAndAccommodationName> findByStatus(StatusEnum accommodationStatus);

    List<AccommodationEntity> findByIdInAndStatus(List<Long> ids, StatusEnum statusEnum);

    @EntityGraph(attributePaths = {"images", "createdBy"})
    Optional<AccommodationEntity> findWithImagesAndImageCreatorByIdAndStatusInAndCreatedByEmail(
            Long accommodationId,
            List<StatusEnum> statuses,
            String email);

    @Override
    Page<AccommodationBasicLikesDto> getAccommodationsWithLikes(
            StatusEnum detailStatus,
            StatusEnum imageStatus,
            String email,
            Pageable pageable,
            Boolean sortByLikedUser);

    @EntityGraph(attributePaths = {"likedByUsers"})
    Optional<AccommodationEntity> findWithLikesByIdAndStatus(Long id, StatusEnum statusEnum);

    Optional<AccommodationEntity> findByIdAndStatusInAndCreatedBy_Email(
            Long accommodationId, List<StatusEnum> detailsStatus, String email);

    @EntityGraph(attributePaths = {"images"})
    Optional<AccommodationEntity> findWithImagesByIdAndStatusInAndCreatedBy_Email(
            Long id,
            List<StatusEnum> status,
            String createdBy_email);

    @EntityGraph(attributePaths = {"images", "images.reviewedBy"})
    Optional<AccommodationEntity> findWithImagesAndImageReviewerById(Long accommodationId);

    @EntityGraph(attributePaths = {"comments"})
    Optional<AccommodationEntity> findWithCommentsByIdAndStatus(Long id, StatusEnum status);

    @EntityGraph(attributePaths = {"comments"})
    Optional<AccommodationEntity> findWithCommentsById(Long id);

    @EntityGraph(attributePaths = {"images", "images.reviewedBy", "reviewedBy"})
    Page<AccommodationForApprovalProjection> getAccommodationEntityByAccommodationStatus(
            SuperUserReviewStatusEnum status,
            Pageable pageable);

    Optional<AccommodationEntity> findByIdAndAccommodationStatus(Long id, SuperUserReviewStatusEnum status);
}
