package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.likeable.LikeableEntity;
import bg.exploreBG.model.dto.LikeBooleanDto;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class LikeService {
    private final UserQueryBuilder userQueryBuilder;

    public LikeService(UserQueryBuilder userQueryBuilder) {
        this.userQueryBuilder = userQueryBuilder;
    }

    public void likeOrUnlikeEntity(
            LikeableEntity likeableEntity,
            LikeBooleanDto likeBoolean,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());
        Set<UserEntity> likedByUsers = likeableEntity.getLikedByUsers();

        boolean userHasLiked = likedByUsers.contains(loggedUser);

        if (likeBoolean.like()) {
            handleLike(likedByUsers, loggedUser, userHasLiked);
        } else {
            handleUnlike(likedByUsers, loggedUser, userHasLiked);
        }
    }

    private void handleLike(Set<UserEntity> likedByUsers, UserEntity user, boolean userHasLiked) {
        if (userHasLiked) {
            throw new AppException("You have already liked the item!", HttpStatus.BAD_REQUEST);
        }
        likedByUsers.add(user);
    }

    private void handleUnlike(Set<UserEntity> likedByUsers, UserEntity user, boolean userHasLiked) {
        if (!userHasLiked) {
            throw new AppException("You cannot unlike an item that you haven't liked!", HttpStatus.BAD_REQUEST);
        }
        likedByUsers.remove(user);
    }
}
