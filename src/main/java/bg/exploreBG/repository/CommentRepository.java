package bg.exploreBG.repository;

import bg.exploreBG.model.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM CommentEntity c
            JOIN c.owner o
            WHERE c.id = :commentId AND o.email = :email
            """)
    boolean isUserOwnerOfComment(@Param("commentId") Long commentId, @Param("email") String email);
}
