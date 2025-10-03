package bg.exploreBG.utils;

import bg.exploreBG.interfaces.UpdatableEntity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class EntityUpdateUtils {
    private EntityUpdateUtils() {}

    public static <T, C extends Collection<T>> boolean updateCollectionIfDifferent(
            Supplier<C> getter,
            Consumer<C> setter,
            C newCollection
    ) {
        C current = getter.get();

        // If both are null, nothing to update
        if (current == null && newCollection == null) {
            return false;
        }

        // If one is null and the other not, they differ
        if (current == null || newCollection == null) {
            setter.accept(newCollection);
            return true;
        }

        // Compare as sets (ignores order, prevents duplicate-sensitive mismatches)
        Set<T> currentSet = new HashSet<>(current);
        Set<T> newSet = new HashSet<>(newCollection);

        if (!Objects.equals(currentSet, newSet)) {
            setter.accept(newCollection);
            return true;
        }

        return false;
    }

    /** Updates a field only if it differs from current value. Returns true if updated. */
    public static <T> boolean updateFieldIfDifferent(Supplier<T> getter, Consumer<T> setter, T newValue) {
        T current = getter.get();
        if (!Objects.equals(current, newValue)) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }

    /** Returns the modification date if the entity was updated, otherwise null */
    public static  <E extends UpdatableEntity> LocalDateTime getModificationDateIfUpdated(E entity, boolean isUpdated) {
        return isUpdated ? entity.getModificationDate() : null;
    }
}
