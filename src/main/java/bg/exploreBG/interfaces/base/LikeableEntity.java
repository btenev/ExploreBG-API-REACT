package bg.exploreBG.interfaces.base;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.UserEntity;
import org.springframework.http.HttpStatus;

import java.util.Set;

public interface LikeableEntity {
    Set<UserEntity> getLikedByUsers();

    default boolean hasUserLiked(UserEntity user) {
        return getLikedByUsers().contains(user);
    }

    default void like(UserEntity user) {
        if (!hasUserLiked(user)) {
            getLikedByUsers().add(user);
        }
    }

    default void unlike(UserEntity user) {
        if (hasUserLiked(user)) {
            getLikedByUsers().remove(user);
        }
    }

    default void likeOrThrow(UserEntity user) {
        if (hasUserLiked(user)) {
            throw new AppException("You have already liked this item and cannot like it again.", HttpStatus.CONFLICT);
        }
        like(user);
    }

    default void unlikeOrThrow(UserEntity user) {
        if (!hasUserLiked(user)) {
            throw new AppException("You can't unlike an item you haven't liked.", HttpStatus.CONFLICT);
        }
        unlike(user);
    }
}
