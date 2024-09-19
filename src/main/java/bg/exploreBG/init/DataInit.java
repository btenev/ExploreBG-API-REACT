package bg.exploreBG.init;

import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInit implements CommandLineRunner {
    private final RoleRepository roleRepository;

    public DataInit(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (this.roleRepository.count() == 0) {
            Arrays.stream(UserRoleEnum.values())
                    .forEach(roleEnum -> {
                        RoleEntity newRole = new RoleEntity();

                        newRole.setRole(roleEnum);

                        this.roleRepository.save(newRole);
                    });
        }
    }
}
