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

        UserEntity persistedUser = this.userRepository.save(newUser);

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

        Optional<UserEntity> currentUser = this.userRepository.findByEmail(foundUser.getUsername());

        return new UserIdNameEmailRolesDto(
                currentUser.get().getId(),
                currentUser.get().getEmail(),
                currentUser.get().getUsername(),
                getRoles(currentUser.get()));
    }

    public UserDetailsOwnerDto findMyProfile(Long id, UserDetails userDetails) {
        UserEntity byId = verifiedUser(id, userDetails);

        return this.userMapper.userEntityToUserDetailsOwnerDto(byId);
    }

    public UserDetailsDto findProfileById(Long id) {
        UserEntity userExist = userExist(id);

        return this.userMapper.userEntityToUserDetailsDto(userExist);
    }

    public UserEmailRolesDto updateEmail(
            Long id,
            UserUpdateEmailDto userUpdateEmailDto,
            UserDetails userDetails
    ) {
        UserEntity byId = verifiedUser(id, userDetails);

        byId.setEmail(userUpdateEmailDto.email());
        UserEntity updatedEmail = this.userRepository.save(byId);

        return new UserEmailRolesDto(updatedEmail.getEmail(), getRoles(updatedEmail));
    }

    public UserUsernameDto updateUsername(
            Long id,
            UserUpdateUsernameDto userUpdateUsernameDto,
            UserDetails userDetails
    ) {
        UserEntity byId = verifiedUser(id, userDetails);

        byId.setUsername(userUpdateUsernameDto.username());
        UserEntity updatedUsername = this.userRepository.save(byId);

        return new UserUsernameDto(updatedUsername.getUsername());
    }

    public SuccessStringDto updatePassword(
            Long id,
            UserUpdatePasswordDto updatePassword,
            UserDetails userDetails
    ) {
        UserEntity byId = verifiedUser(id, userDetails);
        boolean matches = this.passwordEncoder.matches(updatePassword.currentPassword(), userDetails.getPassword());

        if (!matches) {
            throw new AppException("Password do not match!", HttpStatus.FORBIDDEN);
        }

        byId.setPassword(this.passwordEncoder.encode(updatePassword.newPassword()));
        this.userRepository.save(byId);
        return new SuccessStringDto("Password updated successfully!");
    }

    public UserGenderDto updateGender(
            Long id,
            UserUpdateGenderDto userUpdateGenderDto,
            UserDetails userDetails
    ) {
        UserEntity byId = verifiedUser(id, userDetails);
        GenderEnum setGender = userUpdateGenderDto.gender();
        byId.setGender(setGender);

        UserEntity updatedGenderEnum = this.userRepository.save(byId);
        return new UserGenderDto(updatedGenderEnum.getGender().getValue());
    }

    public UserBirthdateDto updateBirthdate(
            Long id,
            UserUpdateBirthdate userBirthdate,
            UserDetails userDetails
    ) {
        UserEntity byId = verifiedUser(id, userDetails);
        byId.setBirthdate(userBirthdate.birthdate());

        UserEntity updatedBirthDate = this.userRepository.save(byId);
        return new UserBirthdateDto(updatedBirthDate.getBirthdate());
    }

    public UserInfoDto updateUserInfo(
            Long id,
            UserUpdateInfo userUpdateInfo,
            UserDetails userDetails
    ) {
        UserEntity byId = verifiedUser(id, userDetails);
        byId.setUserInfo(userUpdateInfo.userInfo());

        UserEntity updatedUserInfo = this.userRepository.save(byId);
        return new UserInfoDto(updatedUserInfo.getUserInfo());
    }

    //TODO: valid user to verified user
    public UserEntity verifiedUser(Long id, UserDetails userDetails) {
        UserEntity byId = userExist(id);
        UserEntity token = userExist(userDetails.getUsername());
        matchUsers(token, byId);
        return byId;
    }

    protected void verifiedUser(UserEntity user, UserDetails userDetails) {
        UserEntity token = userExist(userDetails.getUsername());
        matchUsers(user, token);
    }

    private void matchUsers(UserEntity one, UserEntity two) {
        if (!one.equals(two)) {
            throw new AppException("No access to this resource!", HttpStatus.FORBIDDEN);
        }
    }

    private UserEntity userExist(Long id) {
        Optional<UserEntity> byId = this.userRepository.findById(id);

        if (byId.isEmpty()) {
            throw new AppException("User not found!", HttpStatus.NOT_FOUND);
        }
        return byId.get();
    }

    protected UserEntity userExist(String username) {
        Optional<UserEntity> byEmail = this.userRepository.findByEmail(username);

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

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserDataProjection> getAllUsers(Pageable pageable) {
        return this.userRepository.findAllBy(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserClassDataDto> getAllUsers() {
        Map<Long, UserClassDataDto> userDataDtoMap = new LinkedHashMap<>();

        return this.userRepository.getAllUsers().map(tuple -> {

                    UserClassDataDto userDataDto =
                            userDataDtoMap.computeIfAbsent(tuple.get("id", Long.class),
                                    id -> new UserClassDataDto(
                                            tuple.get("id", Long.class),
                                            tuple.get("username", String.class),
                                            tuple.get("imageUrl", String.class),
                                            tuple.get("creationDate", LocalDateTime.class)
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
    public UserDataDto updateUserRoleToModerator(
            Long id
    ) {
        UserEntity userExist = this.userExist(id);

        List<RoleEntity> userRoles = userExist.getRoles();

        RoleEntity moderator = this.roleService.roleExist(UserRoleEnum.MODERATOR);
        UserEntity saved;

        if (!userRoles.contains(moderator)) {
            userRoles.add(moderator);
            userExist.setRoles(userRoles);
            saved = this.userRepository.save(userExist);
        } else {
            saved = userExist;
        }

        return this.userMapper.userEntityToUserDataDto(saved);
    }
}
