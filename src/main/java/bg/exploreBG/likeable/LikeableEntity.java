package bg.exploreBG.likeable;

import bg.exploreBG.model.entity.UserEntity;

import java.util.Set;

public interface LikeableEntity {
    Set<UserEntity> getLikedByUsers();
}
