package bg.exploreBG.web;

import bg.exploreBG.config.UserAuthProvider;
import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.SuccessStringDto;
import bg.exploreBG.model.dto.user.*;
import bg.exploreBG.model.dto.user.single.*;
import bg.exploreBG.model.dto.user.UserIdNameDto;
import bg.exploreBG.model.dto.user.validate.*;
import bg.exploreBG.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    public UserController(
            UserService userService,
            UserAuthProvider userAuthProvider
    ) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<UserIdNameDto> register(
            @Valid @RequestBody UserRegisterDto userRegisterDto
    ) {
        UserIdNameEmailRolesDto createdUser =
                this.userService.register(userRegisterDto);

        String token =
                this.userAuthProvider.createToken(createdUser.email(), createdUser.roles());

        UserIdNameDto userIdNameDto =
                new UserIdNameDto(createdUser.id(), createdUser.username());

        return ResponseEntity
                .created(URI.create("/api/users/" + createdUser.id() + "/my-profile"))
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(userIdNameDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserIdNameDto> login(
            @RequestBody UserLoginDto userLoginDto
    ) {
        UserIdNameEmailRolesDto loggedUser =
                this.userService.login(userLoginDto);

        String token =
                this.userAuthProvider.createToken(loggedUser.email(), loggedUser.roles());

        UserIdNameDto userIdNameDto =
                new UserIdNameDto(loggedUser.id(), loggedUser.username());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(userIdNameDto);
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<UserDetailsOwnerDto>> myProfile(
//            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        UserDetailsOwnerDto byId =
                this.userService.findMyProfile(userDetails);

        ApiResponse<UserDetailsOwnerDto> response = new ApiResponse<>(byId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<UserDetailsDto>> profile(
            @PathVariable Long id
    ) {
        UserDetailsDto profileById =
                this.userService.findProfileById(id);

        ApiResponse<UserDetailsDto> response = new ApiResponse<>(profileById);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-email")
    public ResponseEntity<ApiResponse<UserEmailDto>> updateEmail(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateEmailDto userUpdateEmailDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserEmailRolesDto newEmail =
                this.userService.updateEmail(id, userUpdateEmailDto, userDetails);

        String token =
                this.userAuthProvider.createToken(newEmail.email(), newEmail.roles());

        UserEmailDto userEmailDto = new UserEmailDto(newEmail.email());

        ApiResponse<UserEmailDto> response = new ApiResponse<>(userEmailDto);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(response);
    }

    @PatchMapping("/{id}/update-username")
    public ResponseEntity<ApiResponse<UserUsernameDto>> updateUsername(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateUsernameDto userUpdateUsernameDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserUsernameDto userUsernameDto =
                this.userService.updateUsername(id, userUpdateUsernameDto, userDetails);

        ApiResponse<UserUsernameDto> response = new ApiResponse<>(userUsernameDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-password")
    public ResponseEntity<ApiResponse<SuccessStringDto>> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdatePasswordDto updatePassword,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuccessStringDto successes =
                this.userService.updatePassword(id, updatePassword, userDetails);

        ApiResponse<SuccessStringDto> response = new ApiResponse<>(successes);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-gender")
    public ResponseEntity<ApiResponse<UserGenderDto>> updateGender(
            @PathVariable Long id,
            @RequestBody UserUpdateGenderDto userUpdateGenderDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserGenderDto userGenderDto =
                this.userService.updateGender(id, userUpdateGenderDto, userDetails);

        ApiResponse<UserGenderDto> response = new ApiResponse<>(userGenderDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-birthdate")
    public ResponseEntity<ApiResponse<UserBirthdateDto>> updateBirthdate(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateBirthdate userUpdateBirthdate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserBirthdateDto userBirthdateDto =
                this.userService.updateBirthdate(id, userUpdateBirthdate, userDetails);

        ApiResponse<UserBirthdateDto> response = new ApiResponse<>(userBirthdateDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/update-user-info")
    public ResponseEntity<ApiResponse<UserInfoDto>> updateUserInfo(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateInfo userUpdateInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserInfoDto userInfoDto =
                this.userService.updateUserInfo(id, userUpdateInfo, userDetails);

        ApiResponse<UserInfoDto> response = new ApiResponse<>(userInfoDto);

        return ResponseEntity.ok(response);
    }
}
