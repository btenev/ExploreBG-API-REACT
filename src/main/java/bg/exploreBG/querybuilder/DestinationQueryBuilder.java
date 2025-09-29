package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.DestinationBasicLikesDto;
import bg.exploreBG.model.dto.destination.DestinationForApprovalProjection;
import bg.exploreBG.model.dto.destination.DestinationIdAndDestinationNameDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.DestinationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DestinationQueryBuilder {
    private final DestinationRepository repository;
    private final Logger logger = LoggerFactory.getLogger(DestinationQueryBuilder.class);

    public DestinationQueryBuilder(DestinationRepository repository) {
        this.repository = repository;
    }

    public List<CommentEntity> getDestinationCommentsById(Long destinationId) {
        return this.repository.findAllCommentsByDestinationId(destinationId);
    }

    public List<DestinationBasicDto> getRandomNumOfDestinations(Pageable pageable) {
        return this.repository.findRandomApprovedDestinations(pageable);
    }

    public List<DestinationBasicLikesDto> getRandomNumOfDestinationsLikes(
            String email,
            int limit
    ) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.repository.findRandomApprovedDestinationsWithLikes(email, pageable);
    }

    public DestinationEntity getDestinationEntityById(Long destinationId) {
        return this.repository.findById(destinationId).orElseThrow(this::destinationNotFoundException);
    }

    public DestinationEntity getDestinationByIdAndStatus(Long destinationId, StatusEnum detailsStatus) {
        return this.repository.findByIdAndStatus(destinationId, detailsStatus)
                .orElseThrow(this::destinationNotFoundOrInvalidStatusException);
    }

    public Page<DestinationBasicDto> getAllDestinationsByStatus(Pageable pageable) {
        return this.repository.findAllByStatus(StatusEnum.APPROVED, pageable);
    }

    public List<DestinationIdAndDestinationNameDto> selectAllApprovedDestinations() {
        return this.repository.findByStatus(StatusEnum.APPROVED);
    }

    public List<DestinationEntity> getDestinationEntitiesByIdsAnStatus(List<Long> ids, StatusEnum status) {
        return this.repository.findByIdInAndStatus(ids, status);
    }

    public int getDestinationCountByStatus(SuperUserReviewStatusEnum status) {
        return this.repository.countDestinationEntitiesByEntityStatus(status);
    }


    public Page<DestinationBasicLikesDto> getAllDestinationsWithLikesByStatus(
            StatusEnum detailsStatus,
            StatusEnum imageStatus,
            String username,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        return this.repository.getDestinationsWithLikes(
                detailsStatus,
                imageStatus,
                username,
                pageable,
                sortByLikedUser);
    }

    public DestinationEntity getDestinationWithLikesByIdAndStatus(Long destinationId, StatusEnum status) {
        return this.repository
                .findWithLikesByIdAndStatus(destinationId, status)
                .orElseThrow(this::destinationNotFoundOrInvalidStatusException);
    }

    public DestinationEntity getDestinationWithImagesAndImageCreatorByIdAndStatusIfOwner(
            Long destinationId,
            List<StatusEnum> statuses,
            String username
    ) {
        return this.repository
                .findWithImagesAndImageCreatorByIdAndStatusInAndCreatedBy_Email(destinationId, statuses, username)
                .orElseThrow(this::destinationNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public DestinationEntity getDestinationWithCommentsByIdAndStatus(Long destinationId, StatusEnum status) {
        return this.repository.findWithCommentsByIdAndStatus(destinationId, status)
                .orElseThrow(this::destinationNotFoundOrInvalidStatusException);
    }

    public DestinationEntity getDestinationWithCommentsById(Long destinationId) {
        return this.repository.findWithCommentsById(destinationId)
                .orElseThrow(this::destinationNotFoundException);
    }

    public DestinationEntity getDestinationByIdAndStatusIfOwner(Long destinationId, String email) {
        return this.repository.findByIdAndStatusInAndCreatedBy_Email(
                        destinationId,
                        List.of(StatusEnum.PENDING, StatusEnum.APPROVED),
                        email)
                .orElseThrow(this::destinationNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public DestinationEntity getDestinationWithImagesByIdAndStatusIfOwner(
            Long destinationId,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.repository
                .findWithIMagesByIdAndStatusInAndCreatedBy_Email(destinationId, statuses, email)
                .orElseThrow(this::destinationNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public Page<DestinationForApprovalProjection> getAllDestinationsByDestinationStatus(
            SuperUserReviewStatusEnum status,
            Pageable pageable
    ) {
        return this.repository.getDestinationsEntitiesByEntityStatus(status, pageable);
    }

    public DestinationEntity getDestinationByIdAndDestinationStatus(
            Long destinationId,
            SuperUserReviewStatusEnum supeStatus
    ) {
        return this.repository.findByIdAndEntityStatus(destinationId, supeStatus)
                .orElseThrow(this::destinationNotFoundOrInvalidStatusException);
    }

    public DestinationEntity getDestinationWithImagesAndImageReviewerById(Long destinationId) {
        return this.repository.findWithImagesAndImageReviewerById(destinationId)
                .orElseThrow(this::destinationNotFoundException);
    }

    public DestinationEntity getDestinationWithImagesByIdIfOwner(Long destinationId, String email) {
        return this.repository.findByIdAndCreatedBy_Email(destinationId, email).orElseThrow(this::destinationNotFoundOrNotOwnerException);
    }

    public void removeUserFromDestinationsByEmail(Long newOwnerId, String oldOwnerEmail) {
        int row = this.repository.removeUserFromDestinationsByEmail(newOwnerId, oldOwnerEmail);
        if (row == 0) {
            this.logger.warn("No destination updated for owner email: {}", oldOwnerEmail);
        }
    }

    private AppException destinationNotFoundException() {
        return new AppException("The destination you are looking for was not found.", HttpStatus.NOT_FOUND);
    }

    public AppException destinationNotFoundOrNotOwnerException() {
        return new AppException("The destination you are looking for was not found or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }

    private AppException destinationNotFoundOrInvalidStatusException() {
        return new AppException("The destination you are looking for was not found or has an invalid status.",
                HttpStatus.NOT_FOUND);
    }

    private AppException destinationNotFoundOrInvalidStatusOrNotOwnerException() {
        return new AppException("The destination you are looking for was not found, has an invalid status, or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }
}