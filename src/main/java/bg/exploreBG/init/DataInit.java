package bg.exploreBG.init;

import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.GenderEnum;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.querybuilder.RoleQueryBuilder;
import bg.exploreBG.service.GenericPersistenceService;
import bg.exploreBG.service.SuperUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class DataInit implements CommandLineRunner {
    private final RoleQueryBuilder roleQueryBuilder;
    private final GenericPersistenceService<RoleEntity> rolePersistence;
    private final GenericPersistenceService<UserEntity> userPersistence;
    private static final Logger logger = LoggerFactory.getLogger(DataInit.class);

    public DataInit(
            RoleQueryBuilder roleQueryBuilder,
            GenericPersistenceService<RoleEntity> rolePersistence,
            GenericPersistenceService<UserEntity> userPersistence
    ) {
        this.roleQueryBuilder = roleQueryBuilder;
        this.rolePersistence = rolePersistence;
        this.userPersistence = userPersistence;
    }

    @Override
    public void run(String... args) {
        if (this.roleQueryBuilder.getRoleEntityCount() == 0) {
            Arrays.stream(UserRoleEnum.values())
                    .forEach(roleEnum -> {
                        RoleEntity newRole = new RoleEntity();

                        newRole.setRole(roleEnum);

                        this.rolePersistence.saveEntityWithoutReturn(newRole);
                    });
        }
        RoleEntity member = this.roleQueryBuilder.getRoleEntityByRoleEnum(UserRoleEnum.ADMIN);
        RoleEntity moderator = this.roleQueryBuilder.getRoleEntityByRoleEnum(UserRoleEnum.MODERATOR);

        UserEntity user = new UserEntity();
        user.setUsername("deletedUser");
        user.setEmail("deleted_user@explore.bg");
        user.setPassword("1234");
        user.setAccountNonLocked(true);
        user.setRoles(Arrays.asList(member, moderator));
        user.setCreationDate(LocalDateTime.now());
        user.setBirthdate(LocalDate.of(2000, 10, 10));
        user.setGender(GenderEnum.MALE);
        logger.info("Deleted user is created: {}", user.getUsername());
        this.userPersistence.saveEntityWithoutReturn(user);
    }
}
