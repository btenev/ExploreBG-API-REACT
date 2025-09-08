package bg.exploreBG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final JwtAuthFilter jwtAuthFilter;
    private final PathValidationFilter pathValidationFilter;

    public SecurityConfig(
            UserAuthenticationEntryPoint userAuthenticationEntryPoint,
            JwtAuthFilter jwtAuthFilter,
            PathValidationFilter pathValidationFilter
    ) {
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
        this.jwtAuthFilter = jwtAuthFilter;
        this.pathValidationFilter = pathValidationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(userAuthenticationEntryPoint))
                .authorizeHttpRequests(req -> {

                    req.requestMatchers(
                            HttpMethod.GET,
                            "/api/accommodations/random",
                            "/api/accommodations/{id:[1-9][0-9]*}",
                            "/api/accommodations",
                            "/api/accommodations/{id:[1-9][0-9]*}/comments",
                            "/api/destinations/random",
                            "/api/destinations/{id:[1-9][0-9]*}",
                            "/api/destinations",
                            "/api/destinations/{id:[1-9][0-9]*}/comments",
                            "/api/hikes/random",
                            "/api/hikes/{id:[1-9][0-9]*}",
                            "/api/hikes",
                            "/api/trails/random",
                            "/api/trails/{id:[1-9][0-9]*}",
                            "/api/trails",
                            "/api/trails/{id:[1-9][0-9]*}/comments",
                            "/api/utilities/**",
                            "/api/users/{id:[1-9][0-9]*}",
                            "/auth/token/refresh"
                    ).permitAll();

                    /*User moderation*/
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/users").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/users/{id:[1-9][0-9]*}/update-role").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/users/{id:[1-9][0-9]*}/lock-account").hasAnyRole("ADMIN", "MODERATOR");

                    req.requestMatchers(HttpMethod.GET, "/api/moderation/entities/waiting-approval/count").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/images/{id:[1-9][0-9]*}/reviewer").hasAnyRole("ADMIN", "MODERATOR");

                    /* Trail review*/
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/trails/waiting-approval").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/trails/{id:[1-9][0-9]*}/reviewer").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/trails/{id:[1-9][0-9]*}/review").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/unclaim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/approve").hasAnyRole("ADMIN", "MODERATOR");
                    /*Trail images review*/
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/images/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/images/approve").hasAnyRole("ADMIN", "MODERATOR");
                    /*Trails gpx-file review*/
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/gpx-file/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/gpx-file/unclaim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/trails/{id:[1-9][0-9]*}/gpx-file/approve").hasAnyRole("ADMIN", "MODERATOR");

                    /*Accommodation review*/
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/accommodations/waiting-approval").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/accommodations/{id:[1-9][0-9]*}/reviewer").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/accommodations/{id:[1-9][0-9]*}/review").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/accommodations/{id:[1-9][0-9]*}/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/accommodations/{id:[1-9][0-9]*}/unclaim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/accommodations/{id:[1-9][0-9]*}/approve").hasAnyRole("ADMIN", "MODERATOR");
                    /*Accommodations images review*/
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/accommodations/{id:[1-9][0-9]*}/images/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/accommodations/{id:[1-9][0-9]*}/gpx-file/unclaim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/accommodations/{id:[1-9][0-9]*}/images/approve").hasAnyRole("ADMIN", "MODERATOR");

                    /*Destinations review*/
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/destinations/waiting-approval").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/destinations/{id:[1-9][0-9]*}/reviewer").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/moderation/destinations/{id:[1-9][0-9]*}/review").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/destinations/{id:[1-9][0-9]*}/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/destinations/{id:[1-9][0-9]*}/unclaim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/destinations/{id:[1-9][0-9]*}/approve").hasAnyRole("ADMIN", "MODERATOR");
                    /*Destinations images review*/
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/destinations/{id:[1-9][0-9]*}/images/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/destinations/{id:[1-9][0-9]*}/gpx-file/unclaim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/moderation/destinations/{id:[1-9][0-9]*}/images/approve").hasAnyRole("ADMIN", "MODERATOR");

                    req.requestMatchers(
                                    HttpMethod.POST,
                                    "/auth/register",
                                    "/auth/login"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(pathValidationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3001",
                "https://explorebg-production.up.railway.app"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.LOCATION,
                HttpHeaders.CONTENT_DISPOSITION
        ));
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                HttpHeaders.LOCATION,
                HttpHeaders.CONTENT_DISPOSITION

        ));
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name()
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
