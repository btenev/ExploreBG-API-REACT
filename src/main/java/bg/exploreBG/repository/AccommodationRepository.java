package bg.exploreBG.repository;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AccommodationRepository extends JpaRepository<AccommodationEntity, Long> {
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

    List<AccommodationBasicDto> findByAccommodationStatus(StatusEnum accommodationStatus);

    List<AccommodationEntity> findByIdInAndAccommodationStatus(List<Long> ids, StatusEnum statusEnum);
}
