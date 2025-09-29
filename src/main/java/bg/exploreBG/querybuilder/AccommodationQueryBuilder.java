package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicLikesDto;
import bg.exploreBG.model.dto.accommodation.AccommodationForApprovalProjection;
import bg.exploreBG.model.dto.accommodation.AccommodationIdAndAccommodationName;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.AccommodationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class AccommodationQueryBuilder {
    private final AccommodationRepository repository;
    private final Logger logger = LoggerFactory.getLogger(AccommodationQueryBuilder.class);

    public AccommodationQueryBuilder(AccommodationRepository repository) {
        this.repository = repository;
    }

    public List<AccommodationBasicDto> getRandomNumOfAccommodations(Pageable pageable) {
        return this.repository.findRandomApprovedAccommodations(pageable);
    }

    public List<AccommodationBasicLikesDto> getRandomNumOfAccommodationLikes(
            String email,
            int limit
    ) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.repository.findRandomApprovedAccommodationsWithLikes(email, pageable);
    }

    public AccommodationEntity getAccommodationEntityById(Long accommodationId) {
        return this.repository.findById(accommodationId).orElseThrow(this::accommodationNotFoundException);
    }

    public AccommodationEntity getAccommodationByIdAndStatus(Long accommodationId, StatusEnum status) {
        return this.repository.findByIdAndStatus(accommodationId, status)
                .orElseThrow(this::accommodationNotFoundOrInvalidStatusException);
    }

    public AccommodationEntity getAccommodationByIdAndStatusIfOwner(Long accommodationId, String email) {
        return this.repository.findByIdAndStatusInAndCreatedBy_Email(
                        accommodationId,
                        List.of(StatusEnum.PENDING, StatusEnum.APPROVED),
                        email)
                .orElseThrow(this::accommodationNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public Page<AccommodationBasicDto> getAllAccommodationsByStatus(StatusEnum statusEnum, Pageable pageable) {
        return this.repository.findAllByStatus(statusEnum, pageable);
    }

    public List<AccommodationIdAndAccommodationName> selectAllApprovedAccommodations() {
        return this.repository.findByStatus(StatusEnum.APPROVED);
    }

    public List<AccommodationEntity> getAccommodationEntitiesByIdAndStatus(List<Long> ids, StatusEnum status) {
        return this.repository.findByIdInAndStatus(ids, status);
    }

    public Page<AccommodationBasicLikesDto> getAllAccommodationsWithLikesByStatus(
            StatusEnum detailsStatus,
            StatusEnum imageStatus,
            String username,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        return this.repository.getAccommodationsWithLikes(
                detailsStatus,
                imageStatus,
                username,
                pageable,
                sortByLikedUser);
    }

    public int getAccommodationCountByAccommodationStatus(SuperUserReviewStatusEnum status) {
        return this.repository.countAccommodationEntitiesByEntityStatus(status);
    }

    public AccommodationEntity getAccommodationWithImagesAndImageReviewerById(Long accommodationId) {
        return this.repository.findWithImagesAndImageReviewerById(accommodationId)
                .orElseThrow(this::accommodationNotFoundException);
    }

    public AccommodationEntity getAccommodationWithImagesByIdIfOwner(Long accommodationId, String email) {
        return this.repository.findByIdAndCreatedBy_Email(accommodationId, email)
                .orElseThrow(this::accommodationNotFoundOrNotOwnerException);
    }

    public AccommodationEntity getAccommodationWithImagesAndImageCreatorByIdAndStatusIfOwner(
            Long accommodationId,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.repository
                .findWithImagesAndImageCreatorByIdAndStatusInAndCreatedByEmail(accommodationId, statuses, email)
                .orElseThrow(this::accommodationNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public AccommodationEntity getAccommodationWithLikesByIdAndStatus(Long accommodationId, StatusEnum status) {
        return this.repository
                .findWithLikesByIdAndStatus(accommodationId, status)
                .orElseThrow(this::accommodationNotFoundOrInvalidStatusException);
    }

    public AccommodationEntity getAccommodationWithImagesByIdAndStatusIfOwner(
            Long accommodationId,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.repository.findWithImagesByIdAndStatusInAndCreatedBy_Email(accommodationId, statuses, email)
                .orElseThrow(this::accommodationNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public AccommodationEntity getAccommodationWithCommentsByIdAndStatus(Long accommodationId, StatusEnum status) {
        return this.repository.findWithCommentsByIdAndStatus(accommodationId, status)
                .orElseThrow(this::accommodationNotFoundOrInvalidStatusException);
    }

    public AccommodationEntity getAccommodationWithCommentsById(Long accommodationId) {
        return this.repository.findWithCommentsById(accommodationId).orElseThrow(this::accommodationNotFoundException);
    }

    public Page<AccommodationForApprovalProjection> getAllAccommodationsByAccommodationStatus(
            SuperUserReviewStatusEnum status,
            Pageable pageable
    ) {
        return this.repository.getAccommodationEntityByEntityStatus(status, pageable);
    }

    public AccommodationEntity getAccommodationByIdAndAccommodationStatus(
            Long accommodationId,
            SuperUserReviewStatusEnum supeStatus
    ) {
        return this.repository.findByIdAndEntityStatus(accommodationId, supeStatus)
                .orElseThrow(this::accommodationNotFoundOrInvalidStatusException);
    }

    public List<CommentEntity> getAccommodationCommentsById(Long accommodationId) {
        return this.repository.findAllCommentsByAccommodationId(accommodationId);
    }

    public void removeUserFromAccommodationsByUserEmailIfOwner(Long newOwnerId, String oldOwnerEmail) {
        int rows = this.repository.removeUserEntityFromAccommodationsByUserEntityEmailIfOwner(newOwnerId, oldOwnerEmail);
        if (rows == 0) {
            this.logger.warn("No accommodations updated for owner email: {}", oldOwnerEmail);
        }
    }

    private AppException accommodationNotFoundException() {
        return new AppException("The accommodation you are looking for was not found.", HttpStatus.NOT_FOUND);
    }

    private AppException accommodationNotFoundOrNotOwnerException() {
        return new AppException("The accommodation you are looking for was not found or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }

    private AppException accommodationNotFoundOrInvalidStatusException() {
        return new AppException("The accommodation you are looking for was not found or has an invalid status.",
                HttpStatus.NOT_FOUND);
    }

    private AppException accommodationNotFoundOrInvalidStatusOrNotOwnerException() {
        return new AppException("The accommodation you are looking for was not found, has an invalid status, or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }
}
