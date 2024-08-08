package bg.exploreBG.repository.custom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;

import java.util.stream.Stream;

public class UserRepositoryCustomImpl implements UserRepositoryCustom {
    @PersistenceContext()
    private EntityManager em;

    @Override
    public Stream<Tuple> getAllUsers() {
        return em.createQuery("""
                        SELECT
                        u.id as id,
                        u.username as username,
                        i.imageUrl as imageUrl,
                        u.creationDate as creationDate,
                        r.role as role
                        FROM UserEntity as u
                        LEFT JOIN u.userImage as i
                        LEFT JOIN u.roles as r
                        """, Tuple.class)
                .getResultStream();
    }
}
