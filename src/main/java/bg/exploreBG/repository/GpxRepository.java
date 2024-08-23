package bg.exploreBG.repository;

import bg.exploreBG.model.entity.GpxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GpxRepository extends JpaRepository<GpxEntity, Long> {
}
