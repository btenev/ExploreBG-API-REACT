package bg.exploreBG.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public class GenericPersistenceServiceImpl<T> implements GenericPersistenceService<T> {
    private final JpaRepository<T, Long> repository;

    public GenericPersistenceServiceImpl(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    @Override
    public void saveEntityWithoutReturn(T entity) {
        this.repository.save(entity);
    }

    @Override
    public T saveEntityWithReturn(T entity) {
        return this.repository.save(entity);
    }

    @Override
    public void saveEntitiesWithoutReturn(Collection<T> entities) {
        this.repository.saveAll(entities);
    }

    @Override
    public List<T> saveEntitiesWithReturn(Collection<T> entities) {
        return this.repository.saveAll(entities);
    }

    @Override
    public void deleteEntityWithoutReturn(T entity) {
        this.repository.delete(entity);
    }

    @Override
    public void deleteEntityWithoutReturnById(Long id) {
        this.repository.deleteById(id);
    }

    @Override
    public void deleteEntitiesWithoutReturn(Collection<T> entities) {
        repository.deleteAll(entities);
    }
}
