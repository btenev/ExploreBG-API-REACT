package bg.exploreBG.service;

import bg.exploreBG.config.CurrentUserProvider;
import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.user.UserDetailsDto;
import bg.exploreBG.model.dto.user.UserDetailsOwnerDto;
import bg.exploreBG.model.dto.user.UserEmailRolesDto;
import bg.exploreBG.model.dto.user.single.UserBirthdateDto;
import bg.exploreBG.model.dto.user.single.UserGenderDto;
import bg.exploreBG.model.dto.user.single.UserInfoDto;
import bg.exploreBG.model.dto.user.single.UserUsernameDto;
import bg.exploreBG.model.dto.user.validate.*;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.mapper.UserMapper;
import bg.exploreBG.querybuilder.*;
import bg.exploreBG.utils.RoleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final GenericPersistenceService<UserEntity> userPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final CommentQueryBuilder commentQueryBuilder;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final HikeQueryBuilder hikeQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final CurrentUserProvider currentUserProvider;

    public UserService(
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            GenericPersistenceService<UserEntity> userPersistence,
            UserQueryBuilder userQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder,
            CommentQueryBuilder commentQueryBuilder,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            HikeQueryBuilder hikeQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder,
            CurrentUserProvider currentUserProvider
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userPersistence = userPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.commentQueryBuilder = commentQueryBuilder;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.hikeQueryBuilder = hikeQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.currentUserProvider = currentUserProvider;
    }

    public UserDetailsOwnerDto findMyProfile() {
        UserEntity user = currentUserProvider.getCurrentUser();
        return this.userMapper.userEntityToUserDetailsOwnerDto(user);
    }

    public UserDetailsDto findProfileById(Long id) {
        UserEntity userExist = this.userQueryBuilder.getUserEntityById(id);
        return this.userMapper.userEntityToUserDetailsDto(userExist);
    }

    public UserEmailRolesDto updateEmail(
            UserUpdateEmailDto dto
    ) {
        return updateUserField(
                dto,
                (user, d) -> user.setEmail(d.email()),
                updated -> new UserEmailRolesDto(updated.getEmail(), RoleUtils.getRoleNames(updated)),
                true
        );
    }

    public UserUsernameDto updateUsername(
            UserUpdateUsernameDto dto
    ) {
        return updateUserField(
                dto,
                (user, d) -> user.setUsername(d.username()),
                (updated -> new UserUsernameDto(updated.getUsername())),
                false
        );
    }

    public Long updatePassword(
            UserUpdatePasswordDto dto
    ) {
        UserEntity user = currentUserProvider.getCurrentUser();
        boolean matches = this.passwordEncoder.matches(dto.currentPassword(), user.getPassword());

        if (!matches) {
            throw new AppException("Password do not match!", HttpStatus.FORBIDDEN);
        }

        user.setPassword(this.passwordEncoder.encode(dto.newPassword()));
        this.userPersistence.saveEntityWithReturn(user);
        return user.getId();
    }

    public UserGenderDto updateGender(
            UserUpdateGenderDto dto
    ) {
        return updateUserField(
                dto,
                (user, d) -> user.setGender(dto.gender()),
                updated -> new UserGenderDto(updated.getGender().getValue()),
                false
        );
    }

    public UserBirthdateDto updateBirthdate(
            UserUpdateBirthdate dto
    ) {
        return updateUserField(
                dto,
                (user, d) -> user.setBirthdate(d.birthdate()),
                updated -> new UserBirthdateDto(updated.getBirthdate()),
                false
        );
    }

    public UserInfoDto updateUserInfo(
            UserUpdateInfo dto
    ) {
        return updateUserField(
                dto,
                (user, d) -> user.setUserInfo(dto.userInfo()),
                updated -> new UserInfoDto(updated.getUserInfo()),
                false
        );
    }

    public boolean isSuperUser(UserDetails userDetails) {
        return userDetails
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_ADMIN")
                                || grantedAuthority.getAuthority().equals("ROLE_MODERATOR"));
    }

    public void deleteAccount(UserDetails userDetails) {
        Long replacementUserId = this.userQueryBuilder.getUserIdByEmail("deleted_user@explore.bg");
        Long currentUserId = this.userQueryBuilder.getUserIdByEmail(userDetails.getUsername());

        this.accommodationQueryBuilder.removeUserFromAccommodationsByUserEmailIfOwner(replacementUserId, userDetails.getUsername());
        this.destinationQueryBuilder.removeUserFromDestinationsByEmail(replacementUserId, userDetails.getUsername());
        this.commentQueryBuilder.removeUserFromCommentsByEmail(replacementUserId, userDetails.getUsername());
        this.hikingTrailQueryBuilder.removeUserEntityFromHikingTrailByEmail(replacementUserId, userDetails.getUsername());
        this.hikeQueryBuilder.removeUserFromHikesByEmail(replacementUserId, userDetails.getUsername());

        /*TODO: add other entities that might have user_id foreign key */

        this.userPersistence.deleteEntityWithoutReturnById(currentUserId);
    }

    private <T, R> R updateUserField(
            T dto,
            BiConsumer<UserEntity, T> updateAction,
            Function<UserEntity, R> resultMapper,
            boolean includeRoles
    ) {
        UserEntity user = includeRoles ? currentUserProvider.getCurrentUserWithRoles() : currentUserProvider.getCurrentUser();
        updateAction.accept(user, dto);
        UserEntity updatedUser = userPersistence.saveEntityWithReturn(user);
        return resultMapper.apply(updatedUser);
    }
}
