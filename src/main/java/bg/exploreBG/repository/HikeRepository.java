package bg.exploreBG.repository;

import bg.exploreBG.model.entity.HikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface HikeRepository extends JpaRepository<HikeEntity, Long> {
    List<HikeEntity> findByIdIn(Set<Long> ids);
}
