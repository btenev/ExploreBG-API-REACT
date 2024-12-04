package bg.exploreBG.repository;

import bg.exploreBG.model.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Transactional
    @Modifying
    @Query("""
            UPDATE CommentEntity c
            SET c.owner.id= :newOwnerId
            WHERE c.owner.email = :email
            """)
    int removeUserEntityFromCommentsByEmail(@Param("newOwnerId") Long newOwnerId, @Param("email") String email);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM CommentEntity c
            JOIN c.owner o
            WHERE c.id = :commentId AND o.email = :email
            """)
    boolean isUserOwnerOfComment(@Param("commentId") Long commentId, @Param("email") String email);

    Optional<CommentEntity> findByIdAndOwnerEmail(Long id, String owner_email);
}
