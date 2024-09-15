package bg.exploreBG.repository.custom;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto;
import bg.exploreBG.model.enums.StatusEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class HikingTrailRepositoryCustomImpl implements HikingTrailRepositoryCustom {
    @PersistenceContext()
    private EntityManager entityManager;

    @Override
    public Page<HikingTrailBasicLikesDto> getTrailsWithLikes(
            StatusEnum statusEnum,
            String email,
            Pageable pageable,
            Boolean sortByLikedUser) {

        // Define the basic JPQL query with filtered LEFT JOIN for the current user's likes
        StringBuilder jpql = new StringBuilder("""
                SELECT new bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicLikesDto(
                    t.id,
                    CONCAT(t.startPoint, ' - ', t.endPoint),
                    t.trailInfo,
                    mi.imageUrl,
                    CASE
                        WHEN lbu.email = :email THEN true
                        ELSE false
                    END
                )
                FROM HikingTrailEntity t
                LEFT JOIN t.mainImage mi
                LEFT JOIN t.likedByUsers lbu ON lbu.email = :email
                WHERE t.trailStatus = :trailStatus
                """);
        Sort sort = pageable.getSort();
        // Apply sorting based on sortByLikedUser or other fields
        if (sortByLikedUser != null && sortByLikedUser) {
            // Sorting by likedByUser
            jpql.append(" ORDER BY CASE WHEN lbu.email = :email THEN 1 ELSE 0 END");
            if (sort.isSorted()) {
                for (Sort.Order order : sort) {
                    if (order.isDescending()) {
                        jpql.append(" DESC");
                    } else {
                        jpql.append(" ASC");
                    }
                }
            }
        } else {
            // Apply sorting based on Pageable (for other fields)

            if (sort.isSorted()) {
                // Iterate through sort orders
                jpql.append(" ORDER BY ");
                List<String> sortClauses = new ArrayList<>();
                for (Sort.Order order : sort) {
                    String property = order.getProperty();
                    String direction = order.isDescending() ? "DESC" : "ASC";

                    // For known fields like `id`, apply sorting directly
                    if ("id".equals(property) || "startPoint".equals(property) || "trailInfo".equals(property)) {
                        sortClauses.add("t." + property + " " + direction);
                    }
                }
                // Append the sort clauses
                jpql.append(String.join(", ", sortClauses));
            } else {
                // Default sorting by id if no sort is specified
                jpql.append(" ORDER BY t.id ASC");
            }
        }

        // Create the query and set parameters
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("trailStatus", statusEnum);
        query.setParameter("email", email);

        // Apply pagination
        int totalResults = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<HikingTrailBasicLikesDto> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, totalResults);
    }

}
