package bg.exploreBG.service;

import bg.exploreBG.config.CurrentUserProvider;
import bg.exploreBG.interfaces.LikeableEntity;
import bg.exploreBG.model.dto.LikeRequestDto;
import bg.exploreBG.model.entity.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private final CurrentUserProvider currentUserProvider;

    public LikeService(CurrentUserProvider currentUserProvider) {
        this.currentUserProvider = currentUserProvider;
    }

    public void likeOrUnlikeEntity(
            LikeableEntity entity,
            LikeRequestDto dto
    ) {
        UserEntity user = currentUserProvider.getCurrentUser();

        if (dto.like()) {
            entity.likeOrThrow(user);
        } else {
            entity.unlikeOrThrow(user);
        }
    }
}
