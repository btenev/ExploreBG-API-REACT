package bg.exploreBG.web;

import bg.exploreBG.config.UserAuthProvider;
import bg.exploreBG.model.dto.user.UserLoginDto;
import bg.exploreBG.model.dto.user.UserRegisterDto;
import bg.exploreBG.model.dto.user.UserDetailsDto;
import bg.exploreBG.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public ResponseEntity<UserDetailsDto> register(@Valid @RequestBody UserRegisterDto userRegisterDto) {
        UserDetailsDto createdUser = this.userService.register(userRegisterDto);
        String token = this.userAuthProvider.createToken(createdUser.email());

        return ResponseEntity
                .created(URI.create("/api/users/" + createdUser.id()))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDetailsDto> login(@RequestBody UserLoginDto userLoginDto) {
        UserDetailsDto loggedUser = this.userService.login(userLoginDto);
        String token = this.userAuthProvider.createToken(loggedUser.email());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(loggedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> profile(@PathVariable Long id) {
        UserDetailsDto byId = this.userService.findById(id);

        return ResponseEntity.ok(byId);
    }
}
