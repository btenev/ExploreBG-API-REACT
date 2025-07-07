package bg.exploreBG.repository;

import bg.exploreBG.model.dto.user.UserDataProjection;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.repository.custom.UserRepositoryCustom;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, UserRepositoryCustom {

    @Query(
    """  
        SELECT u.id
        FROM UserEntity u
        WHERE u.email=:email
    """
    )
    Optional<Long> findUserIdByEmail (@Param("email") String email);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsername(String username);

    @Query("""
            SELECT u
            FROM UserEntity u
            JOIN FETCH u.roles
            WHERE u.id = :id AND SIZE(u.roles) > 0
            """)
    Optional<UserEntity> findWithRolesById (@Param("id") Long userId);

    @Query("""
            SELECT u
            FROM UserEntity u
            JOIN FETCH u.roles
            WHERE u.email = :userEmail AND SIZE(u.roles) > 0
            """)
    Optional<UserEntity> findWithRolesByEmail(@Param("userEmail") String email);

    @PreAuthorize("hasRole('ADMIN')")
    @Query("""
            SELECT u.id AS id, u.username AS username, i.imageUrl AS imageUrl, u.creationDate AS creationDate,
            r AS roles
            FROM UserEntity u
            LEFT JOIN u.userImage as i
            JOIN u.roles AS r
            """)
    Page<UserDataProjection> findAllBy(Pageable pageable);

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Override
    Stream<Tuple> getAllUsers();

    @Query("SELECT u.email FROM UserEntity u WHERE u.id = :id")
    Optional<String> getEmailByUserId(Long id);
}
