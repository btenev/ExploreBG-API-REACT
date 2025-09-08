package bg.exploreBG.service.moderation;

import bg.exploreBG.config.CurrentUserProvider;
import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.role.RoleDto;
import bg.exploreBG.model.dto.user.UserClassDataDto;
import bg.exploreBG.model.dto.user.validate.UserAccountLockRequestDto;
import bg.exploreBG.model.dto.user.validate.UserModRoleDto;
import bg.exploreBG.model.entity.RoleEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.UserRoleEnum;
import bg.exploreBG.model.mapper.RoleMapper;
import bg.exploreBG.querybuilder.*;
import bg.exploreBG.service.GenericPersistenceService;
import bg.exploreBG.utils.RoleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserModerationService {
    private static final Logger logger = LoggerFactory.getLogger(UserModerationService.class);
    private final UserQueryBuilder userQueryBuilder;
    private final RoleQueryBuilder roleQueryBuilder;
    private final CurrentUserProvider currentUserProvider;
    private final GenericPersistenceService<UserEntity> userPersistence;
    private final RoleMapper roleMapper;

    public UserModerationService(
            UserQueryBuilder userQueryBuilder,
            RoleQueryBuilder roleQueryBuilder,
            CurrentUserProvider currentUserProvider,
            GenericPersistenceService<UserEntity> userPersistence,
            RoleMapper roleMapper
    ) {
        this.userQueryBuilder = userQueryBuilder;
        this.roleQueryBuilder = roleQueryBuilder;
        this.currentUserProvider = currentUserProvider;
        this.userPersistence = userPersistence;
        this.roleMapper = roleMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public List<UserClassDataDto> getAllUsers() {
        Map<Long, UserClassDataDto> userDataDtoMap = new LinkedHashMap<>();

        List<UserClassDataDto> result = this.userQueryBuilder.getAllUsers().map(tuple -> {

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
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(result);
        return result;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleDto> addRemoveModeratorRoleToUserRoles(
            Long userId,
            UserModRoleDto mod
    ) {
        UserEntity userToChangeRole = this.userQueryBuilder.getUserEntityByIdWithRoles(userId);
        List<UserRoleEnum> targetRoleEnums = RoleUtils.getUserRoles(userToChangeRole);
        List<RoleEntity> targetRoles = userToChangeRole.getRoles();

        RoleEntity moderator = this.roleQueryBuilder.getRoleEntityByRoleEnum(UserRoleEnum.MODERATOR);
        UserEntity loggedUser = this.currentUserProvider.getCurrentUserWithRoles();

        validateNotSelfAction(userToChangeRole, loggedUser);

        if (targetRoleEnums.contains(UserRoleEnum.ADMIN)) {
            throw new AppException("You cannot  change the role of an ADMIN to MODERATOR.", HttpStatus.BAD_REQUEST);
        }

        if (mod.moderator()) { // add role moderator
            if (targetRoleEnums.contains(UserRoleEnum.MODERATOR)) {
                throw new AppException("The user already has the MODERATOR role.", HttpStatus.BAD_REQUEST);
            }

            targetRoles.add(moderator);
            userToChangeRole.setRoles(targetRoles);
        } else { // remove moderator
            if (!targetRoleEnums.contains(UserRoleEnum.MODERATOR)) {
                throw new AppException("The user does not have the MODERATOR role.", HttpStatus.BAD_REQUEST);
            }

            targetRoles.remove(moderator);
            userToChangeRole.setRoles(targetRoles);
        }

        UserEntity saved = this.userPersistence.saveEntityWithReturn(userToChangeRole);

        logger.info("User {} changed the Moderator role for user {} to: {}", loggedUser.getId(), userToChangeRole.getId(), mod.moderator());
        return this.roleMapper.roleEntityToRoleDto(saved.getRoles());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean lockOrUnlockUserAccount(
            Long userId,
            UserAccountLockRequestDto requestDto
    ) {
        UserEntity userToLock = this.userQueryBuilder.getUserEntityByIdWithRoles(userId);
        List<UserRoleEnum> targetRoles = RoleUtils.getUserRoles(userToLock);

        UserEntity loggedUser = this.currentUserProvider.getCurrentUserWithRoles();
        List<UserRoleEnum> loggedRoles = RoleUtils.getUserRoles(loggedUser);

        validateNotSelfAction(userToLock, loggedUser);

        if (targetRoles.contains(UserRoleEnum.ADMIN)) {
            throw new AppException("You cannot lock the account of an ADMIN.", HttpStatus.BAD_REQUEST);
        }

        boolean bothAreModerators = loggedRoles.contains(UserRoleEnum.MODERATOR) && targetRoles.contains(UserRoleEnum.MODERATOR);

        if (bothAreModerators) {
            throw new AppException("As a MODERATOR, you are not allowed to lock the account of another MODERATOR.", HttpStatus.BAD_REQUEST);
        }

        boolean desiredLockState = requestDto.lockAccount();
        boolean isCurrentlyUnlocked = userToLock.isAccountNonLocked();

        if (desiredLockState) {
            if (!isCurrentlyUnlocked) {
                throw new AppException("The account is already locked.", HttpStatus.BAD_REQUEST);
            }

            userToLock.setAccountNonLocked(false); // Lock it
        } else {
            if (isCurrentlyUnlocked) {
                throw new AppException("The account is already unlocked.", HttpStatus.BAD_REQUEST);
            }

            userToLock.setAccountNonLocked(true); // Unlock it
        }

        this.userPersistence.saveEntityWithReturn(userToLock);

        logger.info("User {} changed lock status of user {} to: {}", loggedUser.getId(), userToLock.getId(), desiredLockState);

        return !userToLock.isAccountNonLocked();
    }

    private void validateNotSelfAction(UserEntity targetUser, UserEntity loggedUser) {
        if (Objects.equals(targetUser.getId(), loggedUser.getId())) {
            throw new AppException("You cannot perform this operation on your own account.", HttpStatus.BAD_REQUEST);
        }
    }
}
