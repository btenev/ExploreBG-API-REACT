package bg.exploreBG.config;

import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Provides access to the currently authenticated user with caching support.
 * The user information is cached within the request scope to avoid multiple database queries.
 */

@Component
@RequestScope
public class CurrentUserProvider {
    private final UserQueryBuilder userQueryBuilder;
    private UserEntity cachedUser;

    public CurrentUserProvider(UserQueryBuilder userQueryBuilder) {
        this.userQueryBuilder = userQueryBuilder;

    }

    public UserEntity getCurrentUser() {
        if (cachedUser == null) {
            cachedUser = userQueryBuilder.getUserEntityByEmail(extractEmailFromPrincipal());
        }
        return cachedUser;
    }

    private String extractEmailFromPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No authentication found in security context");
        }

        var principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        throw new IllegalStateException("Authenticated principal is not of type UserDetails");
    }
}
