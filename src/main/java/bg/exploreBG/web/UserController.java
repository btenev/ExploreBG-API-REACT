package bg.exploreBG.web;

import bg.exploreBG.config.UserAuthProvider;
import bg.exploreBG.model.dto.MessageDto;
import bg.exploreBG.model.dto.user.UserDetailsDto;
import bg.exploreBG.model.dto.user.UserDetailsOwnerDto;
import bg.exploreBG.model.dto.user.UserEmailRolesDto;
import bg.exploreBG.model.dto.user.single.*;
import bg.exploreBG.model.dto.user.validate.*;
import bg.exploreBG.service.AuthService;
import bg.exploreBG.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserAuthProvider userAuthProvider;
    private final AuthService authService;

    public UserController(
            UserService userService,
            UserAuthProvider userAuthProvider,
            AuthService authService
    ) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
        this.authService = authService;
    }

    /*TODO: change name of dto to UserProfileData*/
    @Transactional(readOnly = true)
    @GetMapping("/my-profile")
    public ResponseEntity<UserDetailsOwnerDto> getMyProfile() {
        UserDetailsOwnerDto byId =
                this.userService.findMyProfile();

        return ResponseEntity.ok(byId);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsDto> profile(
            @PathVariable("id") Long userId
    ) {
        UserDetailsDto profile =
                this.userService.findProfileById(userId);

        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/email")
    public ResponseEntity<UserEmailDto> updateEmail(
            @Valid @RequestBody UserUpdateEmailDto userUpdateEmailDto
    ) {
        UserEmailRolesDto newEmail =
                this.userService.updateEmail(userUpdateEmailDto);

        ResponseCookie accessCookie = this.userAuthProvider.getAccessCookie(newEmail.email());

        UserEmailDto userEmailDto = new UserEmailDto(newEmail.email());

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(userEmailDto);
    }

    @PatchMapping("/username")
    public ResponseEntity<UserUsernameDto> updateUsername(
            @Valid @RequestBody UserUpdateUsernameDto userUpdateUsernameDto
    ) {
        UserUsernameDto username =
                this.userService.updateUsername(userUpdateUsernameDto);

        return ResponseEntity.ok(username);
    }

    @PatchMapping("/password")
    public ResponseEntity<MessageDto> updatePassword(
            @Valid @RequestBody UserUpdatePasswordDto updatePassword
    ) {
        Long userId =
                this.userService.updatePassword(updatePassword);

        this.authService.revokeExistingRefreshToken(userId);

        HttpHeaders headers = this.authService.generateEmptyCookies();

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(new MessageDto("Your password has been updated successfully."));
    }

    @PatchMapping("/gender")
    public ResponseEntity<UserGenderDto> updateGender(
            @RequestBody UserUpdateGenderDto userUpdateGenderDto
    ) {
        UserGenderDto userGenderDto =
                this.userService.updateGender(userUpdateGenderDto);

        return ResponseEntity.ok(userGenderDto);
    }

    @PatchMapping("/birthdate")
    public ResponseEntity<UserBirthdateDto> updateBirthdate(
            @Valid @RequestBody UserUpdateBirthdate userUpdateBirthdate
    ) {
        UserBirthdateDto userBirthdateDto =
                this.userService.updateBirthdate(userUpdateBirthdate);

        return ResponseEntity.ok(userBirthdateDto);
    }

    @PatchMapping("/user-info")
    public ResponseEntity<UserInfoDto> updateUserInfo(
            @Valid @RequestBody UserUpdateInfo userUpdateInfo
    ) {
        UserInfoDto userInfoDto =
                this.userService.updateUserInfo(userUpdateInfo);

        return ResponseEntity.ok(userInfoDto);
    }

    /*TODO: not added to frontend*/
    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        this.userService.deleteAccount(userDetails);

        return ResponseEntity.noContent().build();
    }
}
