package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.AccommodationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class AccommodationQueryBuilder {
    private final AccommodationRepository repository;
    private final Logger logger = LoggerFactory.getLogger(AccommodationQueryBuilder.class);

    public AccommodationQueryBuilder(AccommodationRepository repository) {
        this.repository = repository;
    }

    public long getAccommodationCount() {
        return this.repository.count();
    }

    public List<AccommodationBasicPlusImageDto> getAccommodationsByIds(Set<Long> accommodationIds) {
        return this.repository.findByIdIn(accommodationIds);
    }

    public AccommodationEntity getAccommodationEntityById(Long accommodationId) {
        return this.repository.findById(accommodationId).orElseThrow(this::accommodationNotFoundException);
    }

    public Page<AccommodationBasicPlusImageDto> getAllAccommodations(Pageable pageable) {
        return this.repository.findAllBy(pageable);
    }

    public List<AccommodationBasicDto> selectAllApprovedAccommodations() {
        return this.repository.findByStatus(StatusEnum.APPROVED);
    }

    public List<AccommodationEntity> getAccommodationEntitiesByIdAndStatus(List<Long> ids, StatusEnum status) {
        return this.repository.findByIdInAndStatus(ids, status);
    }

    public int getAccommodationCountByAccommodationStatus(SuperUserReviewStatusEnum status) {
        return this.repository.countAccommodationEntitiesByAccommodationStatus(status);
    }

    public void removeUserFromAccommodationsByUserEmailIfOwner(Long newOwnerId, String oldOwnerEmail) {
       int rows = this.repository.removeUserEntityFromAccommodationsByUserEntityEmailIfOwner(newOwnerId, oldOwnerEmail);
       if(rows == 0) {
           this.logger.warn("No accommodations updated for owner email: {}", oldOwnerEmail);
       }
    }

    private AppException accommodationNotFoundException() {
        return new AppException("The accommodation you are looking for was not found.", HttpStatus.NOT_FOUND);
    }
}
