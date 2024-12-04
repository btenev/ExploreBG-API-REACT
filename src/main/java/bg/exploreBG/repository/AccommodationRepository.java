package bg.exploreBG.repository;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface AccommodationRepository extends JpaRepository<AccommodationEntity, Long> {
    @Transactional
    @Modifying
    @Query("""
            UPDATE AccommodationEntity a
            SET a.owner.id= :newOwnerId
            WHERE a.owner.email = :oldOwnerEmail
            """)
    int removeUserEntityFromAccommodationsByUserEntityEmailIfOwner(
            @Param("newOwnerId") Long newOwnerId,
            @Param("oldOwnerEmail") String oldOwnerEmail);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    int countAccommodationEntitiesByAccommodationStatus(SuperUserReviewStatusEnum superUserReviewStatus);

    /*
    @Query("""
       SELECT new bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto(a.id, a.accommodationName, a.imageUrl, a.nextTo)
       FROM AccommodationEntity a
       WHERE a.id IN ?1
    """)   
     */
    List<AccommodationBasicPlusImageDto> findByIdIn(Set<Long> ids);

    Page<AccommodationBasicPlusImageDto> findAllBy(Pageable pageable);

    List<AccommodationBasicDto> findByStatus(StatusEnum accommodationStatus);

    List<AccommodationEntity> findByIdInAndStatus(List<Long> ids, StatusEnum statusEnum);
}
