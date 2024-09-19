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
import bg.exploreBG.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            RoleService roleService,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
    }

    public UserIdNameEmailRolesDto register(UserRegisterDto userRegisterDto) {
        Optional<UserEntity> optionalUserEntity = this.userRepository.findByEmail(userRegisterDto.email());
        if (optionalUserEntity.isPresent()) {
            throw new AppException("User with email " + userRegisterDto.email() + " already exist!",
                    HttpStatus.CONFLICT);
        }

        RoleEntity roleExist = this.roleService.roleExist(UserRoleEnum.MEMBER);

        UserEntity newUser = mapDtoToUserEntity(userRegisterDto, roleExist);
        newUser.setCreationDate(LocalDateTime.now());
        newUser.setAccountNonLocked(true);

        UserEntity persistedUser = saveUserWithReturn(newUser);

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

        Optional<UserEntity> currentUser = this.userRepository.findWithRolesByEmail(foundUser.getUsername());

        return new UserIdNameEmailRolesDto(
                currentUser.get().getId(),
                currentUser.get().getEmail(),
                currentUser.get().getUsername(),
                getRoles(currentUser.get()));
    }

    public UserDetailsOwnerDto findMyProfile(UserDetails userDetails) {
        UserEntity loggedUser = getUserEntityByEmail(userDetails.getUsername());
        return this.userMapper.userEntityToUserDetailsOwnerDto(loggedUser);
    }

    public UserDetailsDto findProfileById(Long id) {
        UserEntity userExist = getUserEntityById(id);

        return this.userMapper.userEntityToUserDetailsDto(userExist);
    }

    public UserEmailRolesDto updateEmail(
            UserUpdateEmailDto userUpdateEmailDto,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = getUserEntityByEmailWithRoles(userDetails.getUsername());

        loggedUser.setEmail(userUpdateEmailDto.email());
        UserEntity updatedEmail = saveUserWithReturn(loggedUser);

        return new UserEmailRolesDto(updatedEmail.getEmail(), getRoles(updatedEmail));
    }

    public UserUsernameDto updateUsername(
            UserUpdateUsernameDto userUpdateUsernameDto,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = getUserEntityByEmail(userDetails.getUsername());

        loggedUser.setUsername(userUpdateUsernameDto.username());
        UserEntity updatedUsername = saveUserWithReturn(loggedUser);

        return new UserUsernameDto(updatedUsername.getUsername());
    }

    public SuccessStringDto updatePassword(
            UserUpdatePasswordDto updatePassword,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = getUserEntityByEmail(userDetails.getUsername());
        boolean matches = this.passwordEncoder.matches(updatePassword.currentPassword(), userDetails.getPassword());

        if (!matches) {
            throw new AppException("Password do not match!", HttpStatus.FORBIDDEN);
        }

        loggedUser.setPassword(this.passwordEncoder.encode(updatePassword.newPassword()));
        saveUserWithoutReturn(loggedUser);
        return new SuccessStringDto("Password updated successfully!");
    }

    public UserGenderDto updateGender(
            UserUpdateGenderDto userUpdateGenderDto,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = getUserEntityByEmail(userDetails.getUsername());
        GenderEnum setGender = userUpdateGenderDto.gender();
        loggedUser.setGender(setGender);

        UserEntity updatedGenderEnum = saveUserWithReturn(loggedUser);
        return new UserGenderDto(updatedGenderEnum.getGender().getValue());
    }

    public UserBirthdateDto updateBirthdate(
            UserUpdateBirthdate userBirthdate,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = getUserEntityByEmail(userDetails.getUsername());
        loggedUser.setBirthdate(userBirthdate.birthdate());

        UserEntity updatedBirthDate = saveUserWithReturn(loggedUser);
        return new UserBirthdateDto(updatedBirthDate.getBirthdate());
    }

    public UserInfoDto updateUserInfo(
            UserUpdateInfo userUpdateInfo,
            UserDetails userDetails
    ) {
        UserEntity loggedUser = getUserEntityByEmail(userDetails.getUsername());
        loggedUser.setUserInfo(userUpdateInfo.userInfo());

        UserEntity updatedUserInfo = saveUserWithReturn(loggedUser);
        return new UserInfoDto(updatedUserInfo.getUserInfo());
    }

    private UserEntity getUserEntityById(Long id) {
        Optional<UserEntity> byId = this.userRepository.findById(id);

        if (byId.isEmpty()) {
            throw new AppException("User not found!", HttpStatus.NOT_FOUND);
        }
        return byId.get();
    }

    protected UserEntity getUserEntityByEmail(String email) {
        Optional<UserEntity> byEmail = this.userRepository.findByEmail(email);

        if (byEmail.isEmpty()) {
            throw new AppException("User not found!", HttpStatus.NOT_FOUND);
        }
        return byEmail.get();
    }

    protected UserEntity getUserEntityByEmailWithRoles(String email) {
        Optional<UserEntity> byEmail = this.userRepository.findWithRolesByEmail(email);

        if (byEmail.isEmpty()) {
            throw new AppException("User not found!", HttpStatus.NOT_FOUND);
        }
        return byEmail.get();
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

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Page<UserDataProjection> getAllUsers(Pageable pageable) {
        return this.userRepository.findAllBy(pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public List<UserClassDataDto> getAllUsers() {
        Map<Long, UserClassDataDto> userDataDtoMap = new LinkedHashMap<>();

        return this.userRepository.getAllUsers().map(tuple -> {

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
        UserEntity userExist = this.getUserEntityById(id);
        List<RoleEntity> userRoles = userExist.getRoles();
        RoleEntity moderator = this.roleService.roleExist(UserRoleEnum.MODERATOR);

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

        UserEntity saved = saveUserWithReturn(userExist);
        return this.userMapper.userEntityToUserDataDto(saved);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean lockOrUnlockUserAccount(
            Long id,
            UserAccountLockUnlockDto lockUnlockDto
    ) {
        UserEntity userExist = getUserEntityById(id);

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

        saveUserWithoutReturn(userExist);
        return true;
    }

    public UserEntity saveUserWithReturn(UserEntity user) {
        return this.userRepository.save(user);
    }

    public void saveUserWithoutReturn(UserEntity user) {
        this.userRepository.save(user);
    }
}
