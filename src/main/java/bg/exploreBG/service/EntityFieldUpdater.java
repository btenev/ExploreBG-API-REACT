package bg.exploreBG.service;

import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.interfaces.UpdatableEntity;
import bg.exploreBG.utils.EntityUpdateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class EntityFieldUpdater {

    /**
     * Updates a simple field only if it differs from current value.
     * Returns DTO mapped result.
     */
    public <E extends UpdatableEntity, T, R> R updateEntityField(
            E entity,
            Supplier<T> getter,
            Consumer<T> setter,
            T newValue,
            Function<E, E> saver,
            BiFunction<E, Boolean, R> dtoMapper
    ) {
        boolean isUpdated = EntityUpdateUtils.updateFieldIfDifferent(getter, setter, newValue);
        return handleUpdate(entity, saver, dtoMapper, isUpdated);
    }

    /**
     * Updates a collection field only if it differs from the current one.
     * Uses mapper to convert DTOs or IDs into entity objects.
     */
    public <E extends UpdatableEntity, I, T extends UpdatableEntity, R> R updateEntityCollection(
            E entity,
            Supplier<List<T>> getter,
            Consumer<List<T>> setter,
            Collection<I> newItems,
            Function<Collection<I>, List<T>> mapper,
            Function<E, E> saver,
            BiFunction<E, Boolean, R> dtoMapper
    ) {
        List<T> mappedNewItems = mapper.apply(newItems);
        boolean isUpdated = EntityUpdateUtils.updateCollectionIfDifferent(getter, setter, mappedNewItems);
        return handleUpdate(entity, saver, dtoMapper, isUpdated);
    }

    /**
     * Centralized update handler for both field and collection updates.
     */
    private <E extends UpdatableEntity, R> R handleUpdate(
            E entity,
            Function<E, E> saver,
            BiFunction<E, Boolean, R> dtoMapper,
            boolean isUpdated
    ) {
        if (isUpdated) {
            entity.setStatus(StatusEnum.PENDING);
            entity.setEntityStatus(SuperUserReviewStatusEnum.PENDING);
            entity.setModificationDate(LocalDateTime.now());
            entity = saver.apply(entity);
        }
        return dtoMapper.apply(entity, isUpdated);
    }
}
