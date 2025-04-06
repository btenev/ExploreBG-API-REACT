package bg.exploreBG.web;

import bg.exploreBG.config.UserAuthProvider;
import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.SuccessStringDto;
import bg.exploreBG.model.dto.user.UserDetailsDto;
import bg.exploreBG.model.dto.user.UserDetailsOwnerDto;
import bg.exploreBG.model.dto.user.UserEmailRolesDto;
import bg.exploreBG.model.dto.user.single.*;
import bg.exploreBG.model.dto.user.validate.*;
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

    public UserController(
            UserService userService,
            UserAuthProvider userAuthProvider
    ) {
        this.userService = userService;
        this.userAuthProvider = userAuthProvider;
    }

    @Transactional(readOnly = true)
    @GetMapping("/my-profile")
    public ResponseEntity<ApiResponse<UserDetailsOwnerDto>> myProfile(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserDetailsOwnerDto byId =
                this.userService.findMyProfile(userDetails);

        ApiResponse<UserDetailsOwnerDto> response = new ApiResponse<>(byId);

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDetailsDto>> profile(
            @PathVariable Long id
    ) {
        UserDetailsDto profileById =
                this.userService.findProfileById(id);

        ApiResponse<UserDetailsDto> response = new ApiResponse<>(profileById);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/email")
    public ResponseEntity<ApiResponse<UserEmailDto>> updateEmail(
            @Valid @RequestBody UserUpdateEmailDto userUpdateEmailDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserEmailRolesDto newEmail =
                this.userService.updateEmail(userUpdateEmailDto, userDetails);

        ResponseCookie accessCookie = this.userAuthProvider.getAccessCookie(newEmail.email());

        UserEmailDto userEmailDto = new UserEmailDto(newEmail.email());

        ApiResponse<UserEmailDto> response = new ApiResponse<>(userEmailDto);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(response);
    }

    @PatchMapping("/username")
    public ResponseEntity<ApiResponse<UserUsernameDto>> updateUsername(
            @Valid @RequestBody UserUpdateUsernameDto userUpdateUsernameDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserUsernameDto userUsernameDto =
                this.userService.updateUsername(userUpdateUsernameDto, userDetails);

        ApiResponse<UserUsernameDto> response = new ApiResponse<>(userUsernameDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<SuccessStringDto>> updatePassword(
            @Valid @RequestBody UserUpdatePasswordDto updatePassword,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuccessStringDto successes =
                this.userService.updatePassword(updatePassword, userDetails);

        ApiResponse<SuccessStringDto> response = new ApiResponse<>(successes);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/gender")
    public ResponseEntity<ApiResponse<UserGenderDto>> updateGender(
            @RequestBody UserUpdateGenderDto userUpdateGenderDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserGenderDto userGenderDto =
                this.userService.updateGender(userUpdateGenderDto, userDetails);

        ApiResponse<UserGenderDto> response = new ApiResponse<>(userGenderDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/birthdate")
    public ResponseEntity<ApiResponse<UserBirthdateDto>> updateBirthdate(
            @Valid @RequestBody UserUpdateBirthdate userUpdateBirthdate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserBirthdateDto userBirthdateDto =
                this.userService.updateBirthdate(userUpdateBirthdate, userDetails);

        ApiResponse<UserBirthdateDto> response = new ApiResponse<>(userBirthdateDto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/user-info")
    public ResponseEntity<ApiResponse<UserInfoDto>> updateUserInfo(
            @Valid @RequestBody UserUpdateInfo userUpdateInfo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        UserInfoDto userInfoDto =
                this.userService.updateUserInfo(userUpdateInfo, userDetails);

        ApiResponse<UserInfoDto> response = new ApiResponse<>(userInfoDto);

        return ResponseEntity.ok(response);
    }

    /*TODO: not added to frontend*/
    @DeleteMapping
    public ResponseEntity<ApiResponse<Boolean>> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.userService.deleteAccount(userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }
}
