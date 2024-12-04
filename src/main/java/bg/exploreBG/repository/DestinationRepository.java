package bg.exploreBG.repository;

import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.model.entity.DestinationEntity;
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
public interface DestinationRepository extends JpaRepository<DestinationEntity, Long> {
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    int countDestinationEntitiesByDestinationStatus(SuperUserReviewStatusEnum status);
    /*                             ---Not necessary.Projection does the job---
           @Query(" SELECT new bg.exploreBG.model.dto.destination.DestinationBasicDto(d.id, d.name, d.imageUrl, d.nextTo) " +
                  " FROM DestinationEntity d" +
                  " WHERE d.id IN ?1")
    */
    List<DestinationBasicPlusDto> findByIdIn(Set<Long> ids);
    Page<DestinationBasicPlusDto> findAllBy(Pageable pageable);
    List<DestinationBasicDto> findByStatus(StatusEnum destinationStatus);
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
}
