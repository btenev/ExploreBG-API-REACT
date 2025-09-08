package bg.exploreBG.web.moderation;

import bg.exploreBG.model.dto.role.RoleDto;
import bg.exploreBG.model.dto.user.UserClassDataDto;
import bg.exploreBG.model.dto.user.single.UserAccountLockResponseDto;
import bg.exploreBG.model.dto.user.validate.UserAccountLockRequestDto;
import bg.exploreBG.model.dto.user.validate.UserModRoleDto;
import bg.exploreBG.service.moderation.UserModerationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moderation/users")
public class UserModerationController {
    private final UserModerationService userModerationService;

    public UserModerationController(
            UserModerationService userModerationService
    ) {
        this.userModerationService = userModerationService;
    }

    /*
     ADMIN
    */
//    @GetMapping("/users")
//    public ResponseEntity<Page<UserDataProjection>> allUsers(
//            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
//            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
//            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
//            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
//    ) {
//        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
//        int currentPage = Math.max(pageNumber - 1, 0);
//
//        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);
//
//        Page<UserDataProjection> users = this.userService.getAllUsers(pageable);
//
//        return ResponseEntity.ok(users);
//    }
    @Transactional
    @GetMapping
    public ResponseEntity<List<UserClassDataDto>> allUsers() {

        List<UserClassDataDto> users = this.userModerationService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/update-role")
    public ResponseEntity<List<RoleDto>> toggleModeratorRole(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UserModRoleDto userModRole
    ) {
        List<RoleDto> role = this.userModerationService.addRemoveModeratorRoleToUserRoles(userId, userModRole);

        return ResponseEntity.ok(role);
    }

    @PatchMapping("/{id}/lock-account")
    public ResponseEntity<UserAccountLockResponseDto> toggleUserAccountLock(
            @PathVariable("id") Long userId,
            @Valid @RequestBody UserAccountLockRequestDto requestDto
    ) {
        boolean lock = this.userModerationService.lockOrUnlockUserAccount(userId, requestDto);

        UserAccountLockResponseDto response = new UserAccountLockResponseDto(lock);

        return ResponseEntity.ok(response);
    }
}
