package bg.exploreBG.repository;

import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.model.entity.DestinationEntity;
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
    List<DestinationBasicDto> findByDestinationStatus(StatusEnum destinationStatus);
    List<DestinationEntity> findAllByIdInAndDestinationStatus(List<Long> ids, StatusEnum statusEnum);
}
