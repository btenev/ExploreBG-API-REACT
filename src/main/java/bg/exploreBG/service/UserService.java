package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.SuccessStringDto;
import bg.exploreBG.model.dto.role.RoleDto;
import bg.exploreBG.model.dto.user.*;
import bg.exploreBG.model.dto.user.single.UserBirthdateDto;
import bg.exploreBG.model.dto.user.single.UserGenderDto;
import bg.exploreBG.model.dto.user.single.UserInfoDto;
import bg.exploreBG.model.dto.user.single.UserUsernameDto;
import bg.exploreBG.model.dto.user.validate.*;
import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.GenderEnum;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.model.mapper.UserMapper;
import bg.exploreBG.querybuilder.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final GenericPersistenceService<UserEntity> userPersistence;
    private final UserQueryBuilder userQueryBuilder;
    private final RoleQueryBuilder roleQueryBuilder;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final CommentQueryBuilder commentQueryBuilder;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final HikeQueryBuilder hikeQueryBuilder;
    private final DestinationQueryBuilder destinationQueryBuilder;

    public UserService(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService,
            UserMapper userMapper,
            GenericPersistenceService<UserEntity> userPersistence,
            UserQueryBuilder userQueryBuilder,
            RoleQueryBuilder roleQueryBuilder,
            AccommodationQueryBuilder accommodationQueryBuilder,
            CommentQueryBuilder commentQueryBuilder,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            HikeQueryBuilder hikeQueryBuilder,
            DestinationQueryBuilder destinationQueryBuilder
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
        this.userPersistence = userPersistence;
        this.userQueryBuilder = userQueryBuilder;
        this.roleQueryBuilder = roleQueryBuilder;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.commentQueryBuilder = commentQueryBuilder;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.hikeQueryBuilder = hikeQueryBuilder;
        this.destinationQueryBuilder = destinationQueryBuilder;
    }

    public UserIdNameEmailRolesDto register(UserRegisterDto userRegisterDto) {
        this.userQueryBuilder.ensureUserEmailAbsent(userRegisterDto.email());

        RoleEntity roleExist = this.roleQueryBuilder.getRoleEntityByRoleEnum(UserRoleEnum.MEMBER);

        UserEntity newUser = mapDtoToUserEntity(userRegisterDto, roleExist);
        newUser.setCreationDate(LocalDateTime.now());
        newUser.setAccountNonLocked(true);

        UserEntity persistedUser = this.userPersistence.saveEntityWithReturn(newUser);

        return new UserIdNameEmailRolesDto(
                persistedUser.getId(),
                persistedUser.getEmail(),
                persistedUser.getUsername(),
                getRoles(persistedUser)
        );
    }

    public UserIdNameEmailRolesDto login(UserLoginDto userLoginDto) {
        UserDetails foundUser = this.userDetailsService.loadUserByUsername(userLoginDto.email());
        boolean matches = this.passwordEncoder.matches(userLoginDto.password(), foundUser.getPassword());

        if (!matches) {
            throw new AppException("Invalid password!", HttpStatus.UNAUTHORIZED);
        }

        UserEntity currentUser = this.userQueryBuilder.getUserEntityByEmailWithRoles(foundUser.getUsername());

        return new UserIdNameEmailRolesDto(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getUsername(),
                getRoles(currentUser));
    }

    public UserDetailsOwnerDto findMyProfile(UserDetails userDetails) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());
        return this.userMapper.userEntityToUserDetailsOwnerDto(loggedUser);
    }

    public UserDetailsDto findProfileById(Long id) {
        UserEntity userExist = this.userQueryBuilder.getUserEntityById(id);

        return this.userMapper.userEntityToUserDetailsDto(userExist);
    }

    public UserEmailRolesDto updateEmail(
            UserUpdateEmailDto userUpdateEmailDto,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmailWithRoles(userDetails.getUsername());

        loggedUser.setEmail(userUpdateEmailDto.email());
        UserEntity updatedEmail = this.userPersistence.saveEntityWithReturn(loggedUser);

        return new UserEmailRolesDto(updatedEmail.getEmail(), getRoles(updatedEmail));
    }

    public UserUsernameDto updateUsername(
            UserUpdateUsernameDto userUpdateUsernameDto,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        loggedUser.setUsername(userUpdateUsernameDto.username());
        UserEntity updatedUsername = this.userPersistence.saveEntityWithReturn(loggedUser);

        return new UserUsernameDto(updatedUsername.getUsername());
    }

    public SuccessStringDto updatePassword(
            UserUpdatePasswordDto updatePassword,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());
        boolean matches = this.passwordEncoder.matches(updatePassword.currentPassword(), userDetails.getPassword());

        if (!matches) {
            throw new AppException("Password do not match!", HttpStatus.FORBIDDEN);
        }

        loggedUser.setPassword(this.passwordEncoder.encode(updatePassword.newPassword()));
        this.userPersistence.saveEntityWithReturn(loggedUser);
        return new SuccessStringDto("Password updated successfully!");
    }

    public UserGenderDto updateGender(
            UserUpdateGenderDto userUpdateGenderDto,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());
        GenderEnum setGender = userUpdateGenderDto.gender();
        loggedUser.setGender(setGender);

        UserEntity updatedGenderEnum = this.userPersistence.saveEntityWithReturn(loggedUser);
        return new UserGenderDto(updatedGenderEnum.getGender().getValue());
    }

    public UserBirthdateDto updateBirthdate(
            UserUpdateBirthdate userBirthdate,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());
        loggedUser.setBirthdate(userBirthdate.birthdate());

        UserEntity updatedBirthDate = this.userPersistence.saveEntityWithReturn(loggedUser);
        return new UserBirthdateDto(updatedBirthDate.getBirthdate());
    }

    public UserInfoDto updateUserInfo(
            UserUpdateInfo userUpdateInfo,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());
        loggedUser.setUserInfo(userUpdateInfo.userInfo());

        UserEntity updatedUserInfo = this.userPersistence.saveEntityWithReturn(loggedUser);
        return new UserInfoDto(updatedUserInfo.getUserInfo());
    }

    private UserEntity mapDtoToUserEntity(UserRegisterDto userRegisterDto, RoleEntity role) {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(userRegisterDto.email());
        newUser.setUsername(userRegisterDto.username());
        newUser.setRoles(Arrays.asList(role));
        newUser.setPassword(passwordEncoder.encode(userRegisterDto.password()));
        return newUser;
    }

    private List<String> getRoles(UserEntity currentUser) {
        return currentUser
                .getRoles()
                .stream()
                .map(r -> r.getRole().name())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public List<UserClassDataDto> getAllUsers() {
        Map<Long, UserClassDataDto> userDataDtoMap = new LinkedHashMap<>();

        return this.userQueryBuilder.getAllUsers().map(tuple -> {

                    UserClassDataDto userDataDto =
                            userDataDtoMap.computeIfAbsent(tuple.get("id", Long.class),
                                    id -> new UserClassDataDto(
                                            tuple.get("id", Long.class),
                                            tuple.get("username", String.class),
                                            tuple.get("imageUrl", String.class),
                                            tuple.get("creationDate", LocalDateTime.class),
                                            tuple.get("accountNonLocked", Boolean.class)
                                    ));
                    userDataDto
                            .getRoles()
                            .add(new RoleDto((tuple.get("role", UserRoleEnum.class))));

                    return userDataDto;
                })
                .distinct()
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserDataDto addRemoveModeratorRoleToUserRoles(
            Long id,
            UserModRoleDto mod
    ) {
        UserEntity userExist = this.userQueryBuilder.getUserEntityById(id);
        List<RoleEntity> userRoles = userExist.getRoles();
        RoleEntity moderator = this.roleQueryBuilder.getRoleEntityByRoleEnum(UserRoleEnum.MODERATOR);

        if (mod.moderator()) { // add role moderator
            if (userRoles.contains(moderator)) {
                throw new AppException("The user is already a moderator!", HttpStatus.BAD_REQUEST);
            }

            userRoles.add(moderator);
            userExist.setRoles(userRoles);
        } else { // remove moderator
            if (!userRoles.contains(moderator)) {
                throw new AppException("You cannot remove the moderator role because the user does not have one!", HttpStatus.BAD_REQUEST);
            }

            userRoles.remove(moderator);
            userExist.setRoles(userRoles);
        }

        UserEntity saved = this.userPersistence.saveEntityWithReturn(userExist);
        return this.userMapper.userEntityToUserDataDto(saved);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean lockOrUnlockUserAccount(
            Long id,
            UserAccountLockUnlockDto lockUnlockDto
    ) {
        UserEntity userExist = this.userQueryBuilder.getUserEntityById(id);

        boolean accountNonLocked = userExist.isAccountNonLocked();

        if (lockUnlockDto.lockAccount()) { // lock account - true
            if (!accountNonLocked) {
                throw new AppException("The account of this user is already locked!", HttpStatus.BAD_REQUEST);
            }

            userExist.setAccountNonLocked(false);
        } else {  // unlock account - false
            if (accountNonLocked) {
                throw new AppException("The account of this user has already been unlocked!", HttpStatus.BAD_REQUEST);
            }

            userExist.setAccountNonLocked(true);
        }

        this.userPersistence.saveEntityWithReturn(userExist);
        return true;
    }

    public boolean isSuperUser(UserDetails userDetails) {
        return userDetails
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_ADMIN")
                                || grantedAuthority.getAuthority().equals("ROLE_MODERATOR"));
    }

    public boolean deleteAccount(UserDetails userDetails) {
        Long replacementUserId = this.userQueryBuilder.getUserIdByEmail("deleted_user@explore.bg");
        Long currentUserId = this.userQueryBuilder.getUserIdByEmail(userDetails.getUsername());

        this.accommodationQueryBuilder.removeUserFromAccommodationsByUserEmailIfOwner(replacementUserId, userDetails.getUsername());
        this.destinationQueryBuilder.removeUserFromDestinationsByEmail(replacementUserId, userDetails.getUsername());
        this.commentQueryBuilder.removeUserFromCommentsByEmail(replacementUserId, userDetails.getUsername());
        this.hikingTrailQueryBuilder.removeUserEntityFromHikingTrailByEmail(replacementUserId, userDetails.getUsername());
        this.hikeQueryBuilder.removeUserFromHikesByEmail(replacementUserId, userDetails.getUsername());

        /*TODO: add other entities that might have user_id foreign key */

        this.userPersistence.deleteEntityWithoutReturnById(currentUserId);
        return true;
    }
}
