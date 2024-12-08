package bg.exploreBG.utils;

import bg.exploreBG.ownableEntity.OwnableEntity;

public final class OwnershipUtils {
    private OwnershipUtils() {}

    public static boolean isOwner(OwnableEntity entity, String username) {
        return entity.getCreatedBy() != null && entity.getCreatedBy().getEmail().equals(username);
    }
}
