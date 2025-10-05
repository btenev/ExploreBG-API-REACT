package bg.exploreBG.service.moderation.common;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.interfaces.composed.ReviewableEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EntityClaimService {

    public <T extends ReviewableEntity> void toggleEntityClaim(
            T entity,
            Boolean claimEntity,
            UserEntity reviewer
    ) {
        if (claimEntity) {
            handleClaimReview(entity, reviewer);
        } else {
            handleCancelClaim(entity, reviewer);
        }
    }

    private <T extends ReviewableEntity> void handleClaimReview(T entity, UserEntity loggedUser) {
        StatusEnum entityStatus = entity.getStatus();

        if (entityStatus == StatusEnum.REVIEW) {
            if (Objects.equals(entity.getReviewedBy().getUsername(), loggedUser.getUsername())) {
                throw new AppException("You have already claimed this item for review!", HttpStatus.BAD_REQUEST);
            } else {
                throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
            }
        }

        validateApprovedStatus(entityStatus);

        entity.setStatus(StatusEnum.REVIEW);
        entity.setReviewedBy(loggedUser);
    }

    private <T extends ReviewableEntity> void handleCancelClaim(T entity, UserEntity loggedUser) {
        StatusEnum entityStatus = entity.getStatus();

        if (entityStatus == StatusEnum.PENDING) {
            throw new AppException("You cannot cancel the review for an item you haven't claimed!", HttpStatus.BAD_REQUEST);
        }

        if (entityStatus == StatusEnum.REVIEW) {
            if (Objects.equals(entity.getReviewedBy().getUsername(), loggedUser.getUsername())) {
                entity.setStatus(StatusEnum.PENDING);
                entity.setReviewedBy(null);
            } else {
                throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
            }
        }

        validateApprovedStatus(entityStatus);
    }

    private void validateApprovedStatus(StatusEnum entityStatus) {
        if (entityStatus == StatusEnum.APPROVED) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }
    }
}
