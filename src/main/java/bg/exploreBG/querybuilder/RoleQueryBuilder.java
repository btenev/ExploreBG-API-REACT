package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class RoleQueryBuilder {
    private final RoleRepository repository;

    public RoleQueryBuilder(RoleRepository repository) {
        this.repository = repository;
    }

    public Long getRoleEntityCount() {
       return this.repository.count();
    }

    public RoleEntity getRoleEntityByRoleEnum(UserRoleEnum userRoleEnum) {
       return this.repository.findByRole(userRoleEnum).orElseThrow(this::roleNotFound);
    }

    private AppException roleNotFound() {
        return new AppException("Role not found!", HttpStatus.NOT_FOUND);
    }
}
