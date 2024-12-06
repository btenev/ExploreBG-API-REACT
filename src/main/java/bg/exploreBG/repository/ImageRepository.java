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

   /* @Query("""
            SELECT COUNT(i.id)
            FROM ImageEntity i
            JOIN HikingTrailEntity ht
            WHERE i MEMBER of ht.images AND ht.id = :hikingTrailId AND i.status <> 'APPROVED'
            """)*/
    @Query("""
            SELECT COUNT(i.id)
            FROM ImageEntity i
            WHERE i.status <> 'APPROVED'
                         AND i.id IN (
                                      SELECT img.id
                                      FROM HikingTrailEntity ht
                                      JOIN ht.images img
                                      WHERE ht.id = :hikingTrailId )
            """)
    long countNonApprovedImagesForTrailId(@Param("hikingTrailId") Long hikingTrailId);

    @Query("""
            SELECT r.id
            FROM ImageEntity i
            JOIN i.reviewedBy r
            WHERE i.id = :imageId
            """)
    Long findReviewerIdByImageId(Long imageId);
}
