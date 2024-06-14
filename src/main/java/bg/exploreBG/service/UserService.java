package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.user.*;
import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.model.mapper.UserMapper;
import bg.exploreBG.repository.RoleRepository;
import bg.exploreBG.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService userDetailsService, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
        this.userMapper = userMapper;
    }

    public UserIdNameEmailDto register(UserRegisterDto userRegisterDto) {
        Optional<UserEntity> optionalUserEntity = this.userRepository.findByEmail(userRegisterDto.email());
        if (optionalUserEntity.isPresent()) {
            throw new AppException("User with email " + userRegisterDto.email()  + " already exist!",
                    HttpStatus.CONFLICT);
        }

        Optional<RoleEntity> optionalRoleEntity = this.roleRepository.findByRole(UserRoleEnum.MEMBER);
        if (optionalRoleEntity.isEmpty()) {
            throw new AppException("User role Member does not exist!", HttpStatus.NOT_FOUND);
        }

        UserEntity newUser = mapDtoToUserEntity(userRegisterDto, optionalRoleEntity);
        UserEntity persistedUser = this.userRepository.save(newUser);

        return new UserIdNameEmailDto(
                persistedUser.getId(),
                persistedUser.getEmail(),
                persistedUser.getUsername()
        );
    }

    public UserIdNameEmailDto login(UserLoginDto userLoginDto) {
        UserDetails foundUser = this.userDetailsService.loadUserByUsername(userLoginDto.email());
        boolean matches = this.passwordEncoder.matches(userLoginDto.password(), foundUser.getPassword());

        if (!matches) {
            throw new AppException("Invalid password!", HttpStatus.UNAUTHORIZED);
        }

        Optional<UserEntity> currentUser = this.userRepository.findByEmail(foundUser.getUsername());

        return new UserIdNameEmailDto(
                currentUser.get().getId(),
                currentUser.get().getEmail(),
                currentUser.get().getEmail()
        );
    }

    public UserDetailsDto findById(Long id, Principal principal) {
        UserEntity byId = validUser(id, principal);

        return this.userMapper.userEntityToUserDetailsDto(byId);
    }

    public UserEmailDto updateEmail(Long id, UserUpdateEmailDto userUpdateEmailDto, Principal principal) {
        UserEntity byId = validUser(id, principal);

        byId.setEmail(userUpdateEmailDto.email());
        UserEntity updatedEmail = this.userRepository.save(byId);

        return new UserEmailDto(updatedEmail.getEmail());
    }

    private UserEntity validUser(Long id, Principal principal) {
        UserEntity byId = userExist(id);
        matchUsers(principal, byId);
        return byId;
    }

    private void matchUsers(Principal principal, UserEntity userEntity) {
        if (!userEntity.getEmail().equals(principal.getName())) {
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
    
    private UserEntity mapDtoToUserEntity(UserRegisterDto userRegisterDto, Optional<RoleEntity> role) {
        UserEntity newUser = new UserEntity();
        newUser.setEmail(userRegisterDto.email());
        newUser.setUsername(userRegisterDto.username());
        newUser.setRoles(Arrays.asList(role.get()));
        newUser.setPassword(passwordEncoder.encode(userRegisterDto.password()));
        return newUser;
    }
    
}
