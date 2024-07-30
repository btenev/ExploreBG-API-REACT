package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.repository.RoleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity roleExist(UserRoleEnum userRoleEnum){
        Optional<RoleEntity> roleExist = this.roleRepository.findByRole(userRoleEnum);

        if(roleExist.isEmpty()) {
            throw new AppException("Role not found!", HttpStatus.NOT_FOUND);
        }

        return roleExist.get();
    }
}
