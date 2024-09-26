package bg.exploreBG.repository;

import bg.exploreBG.model.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    @Query("""
            SELECT i.imageUrl
            FROM ImageEntity i
            JOIN i.profileOwner po
            WHERE po.email = :email
            """)
    Optional<String> findImageUrlByOwnerEmail(@Param("email") String owner_email);
}
