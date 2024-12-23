package bg.exploreBG.repository.custom;

import bg.exploreBG.model.dto.destination.DestinationBasicLikesDto;
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

public class DestinationRepositoryCustomImpl implements DestinationRepositoryCustom {
    @PersistenceContext()
    private EntityManager entityManager;

    @Override
    public Page<DestinationBasicLikesDto> getDestinationsWithLikes(
            StatusEnum detailsStatus,
            StatusEnum imageStatus,
            String email,
            Pageable pageable,
            Boolean sortByLikedUser
    ) {
        // Define the basic JPQL query with filtered LEFT JOIN for the current user's likes
        StringBuilder jpql = new StringBuilder("""
                SELECT new bg.exploreBG.model.dto.destination.DestinationBasicLikesDto(
                d.id,
                d.destinationName,
                mi.imageUrl,
                d.nextTo,
                CASE
                   WHEN lbu.email = :email THEN true
                   ELSE false
                END)
                FROM DestinationEntity d
                LEFT JOIN d.mainImage mi ON mi.status = :imageStatus
                LEFT JOIN d.likedByUsers lbu ON lbu.email = :email
                WHERE d.status = :detailsStatus
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
                    if ("id".equals(property) || "destinationName".equals(property) || "destinationInfo".equals(property)) {
                        sortClauses.add("d." + property + " " + direction);
                    }
                }
                // Append the sort clauses
                jpql.append(String.join(", ", sortClauses));
            } else {
                // Default sorting by id if no sort is specified
                jpql.append(" ORDER BY d.id ASC");
            }
        }

        // Create the query and set parameters
        Query query = entityManager.createQuery(jpql.toString());
        query.setParameter("detailsStatus", detailsStatus);
        query.setParameter("imageStatus", imageStatus);
        query.setParameter("email", email);

        // Apply pagination
        int totalResults = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<DestinationBasicLikesDto> resultList = query.getResultList();
        return new PageImpl<>(resultList, pageable, totalResults);
    }
}
