package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

public class ExploreBgUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public ExploreBgUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> byEmail = this.userRepository.findByEmail(username);

        if (byEmail.isEmpty()) {
            throw new AppException("Unknown user!", HttpStatus.NOT_FOUND);
        }

        UserEntity isPresent = byEmail.get();

        if (!isPresent.isAccountNonLocked()) {
            throw new AppException("Your account has been locked. You temporarily do not have access to it.", HttpStatus.FORBIDDEN);
        }

        return map(isPresent);
    }

    private UserDetails map(UserEntity userEntity) {
        return new ExploreBgUserDetails(
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getUsername(),
                userEntity.isAccountNonLocked(),
                userEntity
                        .getRoles()
                        .stream()
                        .map(this::map)
                        .collect(Collectors.toList())
        );
    }

    private GrantedAuthority map(RoleEntity userRoleEntity) {
        return new SimpleGrantedAuthority("ROLE_" + userRoleEntity.getRole().name());
    }
}
