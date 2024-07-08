package bg.exploreBG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
public class SecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(UserAuthenticationEntryPoint userAuthenticationEntryPoint,
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
                            "/api/accommodations/all",
                            "/api/destinations/*",
                            "/api/hikes/*",
                            "/api/trails/*",
                            "/api/utility/**",
                            "/api/users/*/profile"
                    ).permitAll();

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
        config.setAllowedOrigins(List.of("http://localhost:3000"));
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
