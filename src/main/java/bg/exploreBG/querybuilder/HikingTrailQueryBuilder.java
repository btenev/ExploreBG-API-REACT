package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.HikingTrailRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HikingTrailQueryBuilder {
    private final HikingTrailRepository hikingTrailRepository;

    public HikingTrailQueryBuilder(HikingTrailRepository hikingTrailRepository) {
        this.hikingTrailRepository = hikingTrailRepository;
    }

    public HikingTrailEntity getHikingTrailById(Long trailId) {
        return hikingTrailRepository
                .findById(trailId)
                .orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailWithCommentsById(Long id) {
        return this.hikingTrailRepository
                .findWithCommentsById(id)
                .orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailWithImagesById(Long trailId) {
        return this.hikingTrailRepository
                .findWithImagesById(trailId)
                .orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailByIdIfOwner(Long trailId, String email) {
        return this.hikingTrailRepository
                .findByIdAndCreatedBy_Email(trailId, email)
                .orElseThrow(this::trailNotFoundOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithImagesByIdIfOwner(Long trailId, String email) {
        return this.hikingTrailRepository
                .findWithImagesByIdAndCreatedBy_Email(trailId, email)
                .orElseThrow(this::trailNotFoundOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailByIdAndStatusIfOwner(Long trailId, String email) {
        return this.hikingTrailRepository.findByIdAndStatusInAndCreatedByEmail(
                        trailId,
                        List.of(StatusEnum.PENDING, StatusEnum.APPROVED),
                        email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithImagesByIdAndStatusIfOwner(
            Long trailId,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.hikingTrailRepository.findWithImagesByIdAndStatusInAndCreatedByEmail(
                        trailId, statuses, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithImagesAndImageReviewerByIdAndStatusIfOwner(
            Long trailId,
            List<StatusEnum> statues,
            String email
    ) {
        return this.hikingTrailRepository.findWithImagesAndImageReviewerByIdAndStatusInAndCreatedByEmail(
                        trailId, statues, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithDestinationsByAndStatusIfOwner(
            Long trailId,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.hikingTrailRepository.findWithDestinationsByIdAndStatusInAndCreatedByEmail(
                        trailId, statuses, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithHutsByIdAndStatusIfOwner(
            Long id,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.hikingTrailRepository.findWithHutsByIdAndStatusInAndCreatedByEmail(
                        id, statuses, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithImagesByIdAndTrailStatus(Long id, SuperUserReviewStatusEnum status) {
        return this.hikingTrailRepository.findWithImagesByIdAndTrailStatus(
                        id, status)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    public HikingTrailEntity getHikingTrailWithCommentsByIdAndStatus(Long id, StatusEnum status) {
        return this.hikingTrailRepository
                .findWithCommentsByIdAndStatus(id, status)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    public HikingTrailEntity getHikingTrailWithLikesByIdAndStatus(Long id, StatusEnum status) {
        return this.hikingTrailRepository
                .findWithLikesByIdAndStatus(id, status)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    public HikingTrailEntity getHikingTrailByIdAndStatus(Long id, StatusEnum status) {
        return this.hikingTrailRepository
                .findByIdAndStatus(id, status)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    private AppException trailNotFoundException() {
        return new AppException("The hiking trail you are looking for was not found.", HttpStatus.NOT_FOUND);
    }

    private AppException trailNotFoundOrInvalidStatus() {
        return new AppException("The hiking trail you are looking for was not found or has an invalid status.",
                HttpStatus.BAD_REQUEST);
    }

    private AppException trailNotFoundOrNotOwnerException() {
        return new AppException("The hiking trail you are looking for was not found or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }

    private AppException trailNotFoundOrInvalidStatusOrNotOwnerException() {
        return new AppException("The hiking trail you are looking for was not found, has an invalid status, or does not belong to your account.",
                HttpStatus.BAD_REQUEST);
    }
}
