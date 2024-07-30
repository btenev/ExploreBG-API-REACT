package bg.exploreBG.web;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.user.UserDataDto;
import bg.exploreBG.model.dto.user.UserDataProjection;
import bg.exploreBG.model.dto.user.validate.UserModRoleDto;
import bg.exploreBG.service.HikingTrailService;
import bg.exploreBG.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-users")
public class SuperUserController {

    private final HikingTrailService hikingTrailService;
    private final UserService userService;

    public SuperUserController(
            HikingTrailService hikingTrailService,
            UserService userService
    ) {
        this.hikingTrailService = hikingTrailService;
        this.userService = userService;
    }

    /*
     ADMIN
    */
    @GetMapping("/users")
    public ResponseEntity<Page<UserDataProjection>> allUsers(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<UserDataProjection> users = this.userService.getAllUsers(pageable);

        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/update-role")
    public ResponseEntity<ApiResponse<UserDataDto>> updateRoleToModerator(
            @PathVariable Long id,
            @Valid @RequestBody UserModRoleDto userModRoleDto
    ) {
        if (!userModRoleDto.moderator()) {
            throw new AppException("Invalid request!", HttpStatus.BAD_REQUEST);
        }

        UserDataDto updatedUserRole = this.userService.updateUserRoleToModerator(id);

        ApiResponse<UserDataDto> response = new ApiResponse<>(updatedUserRole);

        return ResponseEntity.ok(response);
    }

    /*
     Moderator
    */
   /*
   @GetMapping("/hiking-trails/review")
    public ResponseEntity<ApiResponse<?>> hikingTrailsReview(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        this.hikingTrailService.getAllHikingTrails(pageable);
    }*/
}
