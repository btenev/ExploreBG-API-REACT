package bg.exploreBG.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Component
public class UserAuthProvider {
    private static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(510);
    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;
    private final UserDetailsService userDetailsService;
    private final CookieProperties cookieProperties;

    public UserAuthProvider(
            UserDetailsService userDetailsService,
            CookieProperties cookieProperties
    ) {
        this.userDetailsService = userDetailsService;
        this.cookieProperties = cookieProperties;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public UsernamePasswordAuthenticationToken validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();

        DecodedJWT decoded = verifier.verify(token);

        UserDetails loadedUserByUsername = this.userDetailsService.loadUserByUsername(decoded.getIssuer());

        return new UsernamePasswordAuthenticationToken(
                loadedUserByUsername,
                loadedUserByUsername.getPassword(),
                loadedUserByUsername.getAuthorities()
        );
    }

    public ResponseCookie getAccessCookie(String userEmail) {
        Instant now = Instant.now();
        String jwtToken = createToken(userEmail, now);

        int maxAge = (int) ACCESS_TOKEN_DURATION.getSeconds();
        return createCookie("access_token", jwtToken, maxAge, "/");
    }

    public ResponseCookie getRefreshTokenCookie() {
        int maxAge = (int) Duration.ofDays(7).toSeconds();
        String refreshToken = generateSecureRefreshToken();
        return createCookie("refresh_token", refreshToken, maxAge, "/auth/refresh-token");
    }

    public ResponseCookie getEmptyAccessCookie() {
        return createCookie("access_token", "", 0, "/");
    }

    public ResponseCookie getEmptyRefreshTokenCookie() {
        return createCookie("refresh_token", "", 0, "/auth/refresh-token");
    }

    private ResponseCookie createCookie(String cookieName, String value, int maxAge, String path) {
        return ResponseCookie
                .from(cookieName, value)
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .maxAge(maxAge)
                .path(path)
                .build();
    }

    private String createToken(String login, Instant issuedAt) {
        Instant expiresAt = issuedAt.plus(ACCESS_TOKEN_DURATION);

        return JWT.create()
                .withIssuer(login)
                .withIssuedAt(Date.from(issuedAt))
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(secretKey));
    }

    private String generateSecureRefreshToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[36];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
