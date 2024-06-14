package bg.exploreBG.web;

import bg.exploreBG.config.UserAuthProvider;
import bg.exploreBG.model.dto.user.*;
import bg.exploreBG.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    public UserController(UserService userService, UserAuthProvider userAuthProvider) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<UserIdDto> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        UserIdPlusEmailDto createdUser = this.userService.register(userRegisterDto);
        String token = this.userAuthProvider.createToken(createdUser.email());

        return ResponseEntity
                .created(URI.create("/api/users/" + createdUser.id() + "/my-profile"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(new UserIdDto(createdUser.id()));
    }

    @PostMapping("/login")
    public ResponseEntity<UserIdDto> login(@RequestBody UserLoginDto userLoginDto) {
        UserIdPlusEmailDto loggedUser = this.userService.login(userLoginDto);
        String token = this.userAuthProvider.createToken(loggedUser.email());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(new UserIdDto(loggedUser.id()));
    }

    @GetMapping("/{id}/my-profile")
    public ResponseEntity<UserDetailsDto> myProfile(@PathVariable Long id, Principal principal) {
        UserDetailsDto byId = this.userService.findById(id, principal);

        return ResponseEntity
                .ok(byId);
    }

    @PatchMapping("/{id}/update-email")
    public ResponseEntity<UserEmailDto> updateEmail(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateEmailDto userUpdateEmailDto,
            Principal principal
    ) {
        UserEmailDto newEmail = this.userService.updateEmail(id, userUpdateEmailDto, principal);
        String token = this.userAuthProvider.createToken(newEmail.email());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(newEmail);
    }
}
