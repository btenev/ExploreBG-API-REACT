package bg.exploreBG.web;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.EntitiesForApprovalCountDto;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.SuccessBooleanDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.user.UserClassDataDto;
import bg.exploreBG.model.dto.user.UserDataDto;
import bg.exploreBG.model.dto.user.validate.UserModRoleDto;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.service.AccommodationService;
import bg.exploreBG.service.DestinationService;
import bg.exploreBG.service.HikingTrailService;
import bg.exploreBG.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-users")
public class SuperUserController {
    private final AccommodationService accommodationService;
    private final DestinationService destinationService;
    private final HikingTrailService hikingTrailService;
    private final UserService userService;

    public SuperUserController(
            AccommodationService accommodationService,
            DestinationService destinationService,
            HikingTrailService hikingTrailService,
            UserService userService
    ) {
        this.accommodationService = accommodationService;
        this.destinationService = destinationService;
        this.hikingTrailService = hikingTrailService;
        this.userService = userService;
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
    @GetMapping("/users")
    public ResponseEntity<List<UserClassDataDto>> allUsers() {

        List<UserClassDataDto> users = this.userService.getAllUsers();

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

    @GetMapping("/waiting-approval/count")
    public ResponseEntity<EntitiesForApprovalCountDto> waitingForApprovalCount() {
        int accommodationCount = this.accommodationService.getPendingApprovalAccommodationCount();
        int destinationCount = this.destinationService.getPendingApprovalDestinationCount();
        int trailCount = this.hikingTrailService.getPendingApprovalTrailCount();

        EntitiesForApprovalCountDto countDto =
                new EntitiesForApprovalCountDto(accommodationCount, destinationCount, trailCount);

        return ResponseEntity.ok(countDto);
    }

    @GetMapping("/waiting-approval/trails")
    public ResponseEntity<Page<HikingTrailForApprovalDto>> waitingForApprovalTrails(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<HikingTrailForApprovalDto> forApproval =
                this.hikingTrailService
                        .getAllHikingTrailsForApproval(List.of(StatusEnum.PENDING, StatusEnum.REVIEW), pageable);

        return ResponseEntity.ok(forApproval);
    }

    //Add data ???
    @Transactional
    @GetMapping("/review/trail/{id}")
    public ResponseEntity<ApiResponse<HikingTrailReviewDto>> reviewNewTrail(
            @PathVariable Long id,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        HikingTrailReviewDto toReview = this.hikingTrailService.reviewTrail(id, exploreBgUserDetails);

        ApiResponse<HikingTrailReviewDto> response = new ApiResponse<>(toReview);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/review/trail/{id}/claim")
    public ResponseEntity<ApiResponse<Boolean>> claimNewTrailReview(
            @PathVariable Long id,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.hikingTrailService.claimTrailReview(id,reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PatchMapping("/approve/trail/{id}")
    public ResponseEntity<ApiResponse<Boolean>> approveNewTrail(
            @PathVariable Long id,
            @Valid @RequestBody HikingTrailCreateOrReviewDto trailCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        boolean approved =
                this.hikingTrailService.approveTrail(id, trailCreateOrReviewDto, exploreBgUserDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(approved);

        return ResponseEntity.ok(response);
    }
}
