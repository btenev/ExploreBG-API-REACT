package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.hikingTrail.*;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.repository.HikingTrailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HikingTrailQueryBuilder {
    private final HikingTrailRepository repository;
    private final Logger logger = LoggerFactory.getLogger(HikingTrailQueryBuilder.class);

    public HikingTrailQueryBuilder(HikingTrailRepository repository) {
        this.repository = repository;
    }

    public List<HikingTrailBasicDto> getRandomNumOfHikingTrails(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.repository.findRandomApprovedTrails(pageable);
    }

    public List<HikingTrailBasicLikesDto> getRandomNumOfHikingTrailsWithLikes(
            String email,
            int limit
    ) {
        Pageable pageable = PageRequest.of(0, limit);
        return this.repository.findRandomApprovedTrailsWithLikes(email, pageable);
    }

    public Page<HikingTrailBasicDto> getAllAHikingTrailsByStatus(StatusEnum statusEnum, Pageable pageable) {
        return this.repository.findAllByTrailStatus(statusEnum, pageable);
    }

    public Page<HikingTrailBasicLikesDto> getAllHikingTrailsWithLikesByStatus(
            StatusEnum statusEnum,
            String email,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        return this.repository.getTrailsWithLikes(statusEnum, email, pageable, sortByLikedUser);
    }

    public List<HikingTrailIdTrailNameDto> selectAll() {
        return this.repository.findAllBy();
    }

    public int getTrailCountByStatus(SuperUserReviewStatusEnum statusEnum) {
        return this.repository.countHikingTrailEntitiesByEntityStatus(statusEnum);
    }

    public Page<HikingTrailForApprovalProjection> getAllHikingTrailsByTrailStatus(
            SuperUserReviewStatusEnum status,
            Pageable pageable
    ) {
        return this.repository.getHikingTrailEntitiesByEntityStatus(status, pageable);
    }

    public Long getReviewerId(Long id) {
        return this.repository.findReviewerId(id);
    }

    public HikingTrailEntity getHikingTrailById(Long trailId) {
        return repository.findById(trailId).orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailWithCommentsById(Long trailId) {
        return this.repository.findWithCommentsById(trailId).orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailWithImagesAndImageReviewerById(Long trailId) {
        return this.repository.findWithImagesById(trailId).orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailByIdIfOwner(Long trailId, String email) {
        return this.repository.findByIdAndCreatedBy_Email(trailId, email)
                .orElseThrow(this::trailNotFoundOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithImagesByIdIfOwner(Long trailId, String email) {
        return this.repository.findWithImagesByIdAndCreatedBy_Email(trailId, email)
                .orElseThrow(this::trailNotFoundOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailByIdAndStatusIfOwner(Long trailId, String email) {
        return this.repository.findByIdAndStatusInAndCreatedByEmail(
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
        return this.repository.findWithImagesByIdAndStatusInAndCreatedByEmail(trailId, statuses, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithImagesAndImageCreatorByIdAndStatusIfOwner(
            Long trailId,
            List<StatusEnum> statues,
            String email
    ) {
        return this.repository.findWithImagesAndImageCreatorByIdAndStatusInAndCreatedByEmail(trailId, statues, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithDestinationsByAndStatusIfOwner(
            Long trailId,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.repository.findWithDestinationsByIdAndStatusInAndCreatedByEmail(trailId, statuses, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithHutsByIdAndStatusIfOwner(
            Long trailId,
            List<StatusEnum> statuses,
            String email
    ) {
        return this.repository.findWithHutsByIdAndStatusInAndCreatedByEmail(trailId, statuses, email)
                .orElseThrow(this::trailNotFoundOrInvalidStatusOrNotOwnerException);
    }

    public HikingTrailEntity getHikingTrailWithCommentsByIdAndStatus(Long trailId, StatusEnum status) {
        return this.repository.findWithCommentsByIdAndStatus(trailId, status)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    public HikingTrailEntity getHikingTrailWithLikesByIdAndStatus(Long trailId, StatusEnum status) {
        return this.repository.findWithLikesByIdAndStatus(trailId, status)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    public HikingTrailEntity getHikingTrailByIdAndStatus(Long trailId, StatusEnum status) {
        return this.repository.findByIdAndStatus(trailId, status)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    public HikingTrailEntity getHikingTrailByIdAndTrailStatus(Long trailId, SuperUserReviewStatusEnum supeStatus) {
        return this.repository.findByIdAndEntityStatus(trailId, supeStatus)
                .orElseThrow(this::trailNotFoundOrInvalidStatus);
    }

    public HikingTrailEntity getHikingTrailWithImagesAndImageReviewerAndGpxFileById(Long trailId) {
        return this.repository.findWithImageAndGpxFileById(trailId)
                .orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailImageStatusAndGpxFileStatus getHikingTrailImageStatusAndGpxStatusById(Long trailId) {
        return this.repository.findImageStatusAndGpxStatusById(trailId)
                .orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailWithGpxFileById(Long trailId) {
        return this.repository.findWithGpxFileAndReviewerById(trailId)
                .orElseThrow(this::trailNotFoundException);
    }

    public HikingTrailEntity getHikingTrailWithHikesByIdIfOwner(Long trailId, String email) {
        return this.repository.findWithHikesHikingTrailByIdAndCreatedByEmail(trailId, email)
                .orElseThrow(this::trailNotFoundOrNotOwnerException);
    }

    public void removeUserEntityFromHikingTrailByEmail(Long newOwnerId, String oldOwnerEmail) {
        int row = this.repository.removeUserEntityFromHikingTrailByEmail(newOwnerId, oldOwnerEmail);
        if(row == 0) {
            this.logger.warn("No hiking trail updated for owner email: {}", oldOwnerEmail);
        }
    }

    public List<CommentEntity> getHikingTrailCommentsById(Long trailId) {
        return this.repository.findAllCommentsByTrailId(trailId);
    }

    private AppException trailNotFoundException() {
        return new AppException("The hiking trail you are looking for was not found.", HttpStatus.NOT_FOUND);
    }

    private AppException trailNotFoundOrInvalidStatus() {
        return new AppException("The hiking trail you are looking for was not found or has an invalid status.",
                HttpStatus.NOT_FOUND);
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
