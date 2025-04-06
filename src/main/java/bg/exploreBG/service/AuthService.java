package bg.exploreBG.service;

import bg.exploreBG.config.UserAuthProvider;
import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.user.UserAuthDto;
import bg.exploreBG.model.dto.user.UserLoginDto;
import bg.exploreBG.model.dto.user.validate.UserRegisterDto;
import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.querybuilder.RoleQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.utils.RoleUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class AuthService {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserQueryBuilder userQueryBuilder;
    private final RoleQueryBuilder roleQueryBuilder;
    private final GenericPersistenceService<UserEntity> userPersistence;
    private final UserAuthProvider userAuthProvider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            UserQueryBuilder userQueryBuilder,
            RoleQueryBuilder roleQueryBuilder,
            GenericPersistenceService<UserEntity> userPersistence,
            UserAuthProvider userAuthProvider,
            RefreshTokenService refreshTokenService
    ) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userQueryBuilder = userQueryBuilder;
        this.roleQueryBuilder = roleQueryBuilder;
        this.userPersistence = userPersistence;
        this.userAuthProvider = userAuthProvider;
        this.refreshTokenService = refreshTokenService;
    }

    public UserAuthDto login(UserLoginDto userLoginDto) {
        UserDetails foundUser = this.userDetailsService.loadUserByUsername(userLoginDto.email());
        boolean matches = this.passwordEncoder.matches(userLoginDto.password(), foundUser.getPassword());

        if (!matches) {
            throw new AppException("Invalid password!", HttpStatus.UNAUTHORIZED);
        }

        UserEntity currentUser = this.userQueryBuilder.getUserEntityByEmailWithRoles(foundUser.getUsername());

        return new UserAuthDto(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getUsername(),
                currentUser.getUserImage().getImageUrl(),
                RoleUtils.getRoleNames(currentUser)
        );
    }

    public UserAuthDto register(UserRegisterDto userRegisterDto) {
        this.userQueryBuilder.ensureUserEmailAbsent(userRegisterDto.email());

        RoleEntity roleExist = this.roleQueryBuilder.getRoleEntityByRoleEnum(UserRoleEnum.MEMBER);

        UserEntity newUser = mapDtoToUserEntity(userRegisterDto, roleExist);
        newUser.setCreationDate(LocalDateTime.now());
        newUser.setAccountNonLocked(true);

        UserEntity persistedUser = this.userPersistence.saveEntityWithReturn(newUser);

        return new UserAuthDto(
                persistedUser.getId(),
                persistedUser.getEmail(),
                persistedUser.getUsername(),
                null,
                RoleUtils.getRoleNames(persistedUser)
        );
    }

    public HttpHeaders generateAuthCookies(Long userId, String email) {
        ResponseCookie accessCookie = this.userAuthProvider.getAccessCookie(email);
        ResponseCookie refreshCookie = this.userAuthProvider.getRefreshTokenCookie();
        String refreshToken = refreshCookie.getValue();

        refreshTokenService.storeToken(refreshToken, userId, Duration.ofDays(7));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return headers;
    }

    public String findEmailByUserId(Long id) {
        return this.userQueryBuilder.getEmailByUserId(id);
    }

    private UserEntity mapDtoToUserEntity(UserRegisterDto userRegisterDto, RoleEntity role) {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(userRegisterDto.email());
        newUser.setUsername(userRegisterDto.username());
        newUser.setRoles(Collections.singletonList(role));
        newUser.setPassword(passwordEncoder.encode(userRegisterDto.password()));
        return newUser;
    }
}
