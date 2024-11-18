package bg.exploreBG.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PathValidationFilter extends OncePerRequestFilter {
    private final List<Pattern> PATH_PATTERNS = List.of(
            Pattern.compile("/api/super-users/(-?\\d+)/.*"),
            Pattern.compile("/api/super-users/trails/(-?\\d+)/.*")
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();

        for (Pattern pattern : PATH_PATTERNS) {
            Matcher matcher = pattern.matcher(path);

            if (matcher.matches()) {
                long id = Long.parseLong(matcher.group(1));

                if (id <= 0) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"message\": \"Invalid path with negative ID\"}");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
