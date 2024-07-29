package bg.exploreBG.repository;

import bg.exploreBG.model.dto.user.UserAllProjection;
import bg.exploreBG.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);

    @PreAuthorize("hasRole('ADMIN')")
    @Query("""
            SELECT u.id AS id, u.username AS username, i.imageUrl AS imageUrl, u.creationDate AS creationDate, r AS roles
            FROM UserEntity u
            LEFT JOIN u.userImage as i
            LEFT JOIN u.roles r
            """)
    Page<UserAllProjection> findAllBy(Pageable pageable);
}
