package bg.exploreBG.interfaces.base;

import bg.exploreBG.model.entity.UserEntity;

public interface OwnableEntity  {
    UserEntity getCreatedBy();
}
