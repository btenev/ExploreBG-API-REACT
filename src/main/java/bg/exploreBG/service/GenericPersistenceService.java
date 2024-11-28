package bg.exploreBG.service;

import java.util.Collection;
import java.util.List;

public interface GenericPersistenceService<T> {
    void saveEntityWithoutReturn(T entity);

    T saveEntityWithReturn(T entity);

    void saveEntitiesWithoutReturn(Collection<T> entities);

    List<T> saveEntitiesWithReturn(Collection<T> entities);

    void deleteEntityWithoutReturn(T entity);

    void deleteEntityWithoutReturnById(Long id);

    void deleteEntitiesWithoutReturn(Collection<T> entities);
}
