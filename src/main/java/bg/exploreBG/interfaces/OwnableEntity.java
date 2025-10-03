package bg.exploreBG.interfaces;

import bg.exploreBG.model.entity.UserEntity;

public interface OwnableEntity  {
    UserEntity getCreatedBy();
}
