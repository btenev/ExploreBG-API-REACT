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

    public SecurityConfig(
            UserAuthenticationEntryPoint userAuthenticationEntryPoint,
            JwtAuthFilter jwtAuthFilter
    ) {
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
        this.jwtAuthFilter = jwtAuthFilter;
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
                            "/api/accommodations/{id:[1-9]+}",
                            "/api/accommodations",
                            "/api/destinations/random",
                            "/api/destinations/{id:[1-9]+}",
                            "/api/destinations",
                            "/api/hikes/random",
                            "/api/hikes/{id:[1-9]+}",
                            "/api/hikes",
                            "/api/trails/random",
                            "/api/trails/{id:[1-9]+}",
                            "/api/trails",
                            "/api/utility/**",
                            "/api/users/{id:[1-9]+}"
                    ).permitAll();

                    req.requestMatchers(HttpMethod.GET, "/api/super-users/users").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/super-users/{id:[1-9]+}/update-role").hasRole("ADMIN");
                    req.requestMatchers(HttpMethod.GET, "/api/super-users/waiting-approval/count").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/super-users/waiting-approval/trails").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/super-users/trails/{id:[1-9]+}/review").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.GET, "/api/super-users/trails/{id:[1-9]+}/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/super-users/trails/{id:[1-9]+}/approve").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/super-users/trails/{id:[1-9]+}/images/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/super-users/trails/{id:[1-9]+}/images/approve").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/super-users/trails/{id:[1-9]+}/gpx-file/claim").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/super-users/trails/{id:[1-9]+}/gpx-file/approve").hasAnyRole("ADMIN", "MODERATOR");
                    req.requestMatchers(HttpMethod.PATCH, "/api/super-users/{id:[1-9]+}/lock-account").hasAnyRole("ADMIN", "MODERATOR");

                    req.requestMatchers(
                                    HttpMethod.POST,
                                    "/api/users/register",
                                    "/api/users/login"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
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
