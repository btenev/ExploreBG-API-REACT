package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.DestinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DestinationQueryBuilder {
    private final DestinationRepository repository;
    private final Logger logger = LoggerFactory.getLogger(DestinationQueryBuilder.class);

    public DestinationQueryBuilder(DestinationRepository repository) {
        this.repository = repository;
    }

    public long getDestinationCount() {
        return this.repository.count();
    }

    public List<DestinationBasicPlusDto> getDestinationsByIds (Set<Long> destinationIds) {
        return this.repository.findByIdIn(destinationIds);
    }

    public DestinationEntity getDestinationById(Long destinationId) {
        return this.repository.findById(destinationId).orElseThrow(this::destinationNotFound);
    }

    public Page<DestinationBasicPlusDto> getAllDestinations(Pageable pageable) {
        return this.repository.findAllBy(pageable);
    }

    public List<DestinationBasicDto> selectAllApprovedDestinations() {
        return this.repository.findByStatus(StatusEnum.APPROVED);
    }

    public List<DestinationEntity> getDestinationEntitiesByIdsAnStatus(List<Long> ids, StatusEnum status) {
        return this.repository.findByIdInAndStatus(ids, status);
    }

    public int getDestinationCountByStatus(SuperUserReviewStatusEnum status) {
        return this.repository.countDestinationEntitiesByDestinationStatus(status);
    }

    public void removeUserFromDestinationsByEmail(Long newOwnerId, String oldOwnerEmail) {
        int row = this.repository.removeUserFromDestinationsByEmail(newOwnerId, oldOwnerEmail);
        if(row == 0) {
            this.logger.warn("No destination updated for owner email: {}", oldOwnerEmail);
        }
    }

    private AppException destinationNotFound() {
        return new AppException("The accommodation you are looking for was not found.", HttpStatus.NOT_FOUND);
    }
}
