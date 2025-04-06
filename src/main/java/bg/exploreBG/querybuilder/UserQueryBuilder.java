package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.repository.UserRepository;
import jakarta.persistence.Tuple;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class UserQueryBuilder {
    private final UserRepository repository;

    public UserQueryBuilder(UserRepository repository) {
        this.repository = repository;
    }

    public void ensureUserEmailAbsent(String email) {
        if (this.repository.findByEmail(email).isPresent()) {
            throw new AppException("User with email " + email + " already exists!", HttpStatus.CONFLICT);
        }
    }
    public UserEntity getUserEntityByEmail(String email) {
        return this.repository.findByEmail(email).orElseThrow(this::userNotFound);
    }

    public UserEntity getUserEntityById(Long id) {
        return this.repository.findById(id).orElseThrow(this::userNotFound);
    }

    public UserEntity getUserEntityByEmailWithRoles(String email) {
        return this.repository.findWithRolesByEmail(email).orElseThrow(this::userNotFoundOrRolesMissing);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Stream<Tuple> getAllUsers() {
        return this.repository.getAllUsers();
    }

    public Long getUserIdByEmail(String email) {
        return this.repository.findUserIdByEmail(email).orElseThrow(this::userNotFound);
    }

    public String getEmailByUserId(Long id) {
        return this.repository.getEmailByUserId(id).orElseThrow(this::userNotFound);
    }

    private AppException userNotFound() {
        return new AppException("User not found.", HttpStatus.NOT_FOUND);
    }

    private AppException userNotFoundOrRolesMissing() {
        return new AppException("User not found or user roles are missing.", HttpStatus.NOT_FOUND);
    }
}
