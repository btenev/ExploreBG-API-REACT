package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.BaseEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class ReviewService {
    public <T extends BaseEntity> void handleClaimReview (T entity, UserEntity loggedUser) {
        if (entity.getStatus() == StatusEnum.REVIEW) {
            if (Objects.equals(entity.getReviewedBy().getUsername(), loggedUser.getUsername())) {
                throw new AppException("You have already claimed this item for review!", HttpStatus.BAD_REQUEST);
            } else {
                throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
            }
        }

        if (entity.getStatus() == StatusEnum.APPROVED) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }

        entity.setStatus(StatusEnum.REVIEW);
        entity.setReviewedBy(loggedUser);
    }

    public <T extends BaseEntity> void handleCancelClaim (T entity, UserEntity loggedUser) {
        if (entity.getStatus() == StatusEnum.PENDING) {
            throw new AppException("You cannot cancel the review for an item you haven't claimed!", HttpStatus.BAD_REQUEST);
        }

        if (entity.getStatus() == StatusEnum.REVIEW) {
            if (Objects.equals(entity.getReviewedBy().getUsername(), loggedUser.getUsername())) {
                entity.setStatus(StatusEnum.PENDING);
                entity.setReviewedBy(null);
            } else {
                throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
            }
        }

        if (entity.getStatus() == StatusEnum.APPROVED) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }
    }
}
