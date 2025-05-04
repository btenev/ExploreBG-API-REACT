package bg.exploreBG.web;

import bg.exploreBG.config.UserAuthProvider;
import bg.exploreBG.model.dto.MessageDto;
import bg.exploreBG.model.dto.user.UserAuthDto;
import bg.exploreBG.model.dto.user.UserSessionDto;
import bg.exploreBG.model.dto.user.UserSessionNoImageDto;
import bg.exploreBG.model.dto.user.validate.UserLoginDto;
import bg.exploreBG.model.dto.user.validate.UserRegisterDto;
import bg.exploreBG.service.AuthService;
import bg.exploreBG.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final UserAuthProvider userAuthProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthService authService,
            UserAuthProvider userAuthProvider,
            RefreshTokenService refreshTokenService
    ) {
        this.authService = authService;
        this.userAuthProvider = userAuthProvider;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserSessionDto> login(
            @Valid @RequestBody UserLoginDto userLoginDto
    ) {
        UserAuthDto logged = this.authService.login(userLoginDto);
        this.authService.revokeExistingRefreshToken(logged.id());

        HttpHeaders headers = this.authService.generateAuthCookies(logged.id(), logged.email());
        UserSessionDto session =
                new UserSessionDto(logged.id(), logged.username(), logged.imageUrl(), logged.roles());

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(session);
    }

    @PostMapping("/register")
    public ResponseEntity<UserSessionNoImageDto> register(
            @Valid @RequestBody UserRegisterDto userRegisterDto
    ) {
        UserAuthDto created =
                this.authService.register(userRegisterDto);

        HttpHeaders headers = this.authService.generateAuthCookies(created.id(), created.email());
        UserSessionNoImageDto session =
                new UserSessionNoImageDto(created.id(), created.username(), created.roles());

        return ResponseEntity
                .created(URI.create("/api/users/" + created.id() + "/my-profile"))
                .headers(headers)
                .body(session);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(@CookieValue("refresh_token") String refreshToken) {
        Optional<Long> userId = this.refreshTokenService.validateToken(refreshToken);

        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userEmail = this.authService.findEmailByUserId(userId.get());

        ResponseCookie accessCookie = this.userAuthProvider.getAccessCookie(userEmail);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDto> logout(@AuthenticationPrincipal UserDetails userDetails) {

        Long userId = this.authService.findUserIdByEmail(userDetails.getUsername());
        this.refreshTokenService.revokeExistingRefreshTokenAtomic(userId);

        HttpHeaders headers = this.authService.generateEmptyCookies();

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new MessageDto("You have been logged out successfully."));
    }
}
