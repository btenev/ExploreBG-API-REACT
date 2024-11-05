package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.EntitiesForApprovalUnderReviewCountDto;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationApprovalReviewCountDto;
import bg.exploreBG.model.dto.accommodation.DestinationApprovalReviewCountDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.TrailApprovalReviewCountDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.dto.user.UserClassDataDto;
import bg.exploreBG.model.dto.user.UserDataDto;
import bg.exploreBG.model.dto.user.validate.UserAccountLockUnlockDto;
import bg.exploreBG.model.dto.user.validate.UserModRoleDto;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.service.*;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-users")
public class SuperUserController {
    private static final String TRAILFOLDER = "Trails";
    private final AccommodationService accommodationService;
    private final DestinationService destinationService;
    private final HikingTrailService hikingTrailService;
    private final UserService userService;
    private final SuperUserService superUserService;

    public SuperUserController(
            AccommodationService accommodationService,
            DestinationService destinationService,
            HikingTrailService hikingTrailService,
            UserService userService,
            SuperUserService superUserService
    ) {
        this.accommodationService = accommodationService;
        this.destinationService = destinationService;
        this.hikingTrailService = hikingTrailService;
        this.userService = userService;
        this.superUserService = superUserService;
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

    /*TODO: IVO: only messages, no errors*/
    @PatchMapping("/{id}/update-role")
    public ResponseEntity<ApiResponse<UserDataDto>> toggleModeratorRole(
            @PathVariable("id") Long userId,
            @RequestBody UserModRoleDto userModRoleDto
    ) {

        UserDataDto updatedUserRole = this.userService.addRemoveModeratorRoleToUserRoles(userId, userModRoleDto);

        ApiResponse<UserDataDto> response = new ApiResponse<>(updatedUserRole);

        return ResponseEntity.ok(response);
    }

    /*TODO: IVO: only messages, no errors
      url: /api/super-users/{id}/lock
     */
    @PatchMapping("/{id}/lock-account")
    public ResponseEntity<ApiResponse<Boolean>> toggleUserAccountLock(
            @PathVariable("id") Long userId,
            @RequestBody UserAccountLockUnlockDto userAccountLockUnlockDto
    ) {
        boolean success = this.userService.lockOrUnlockUserAccount(userId, userAccountLockUnlockDto);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    /*TODO:  old: /waiting-approval/count new: /entities/waiting-approval/count*/
    @GetMapping("/entities/waiting-approval/count")
    public ResponseEntity<EntitiesForApprovalUnderReviewCountDto> waitingForApprovalUnderReviewCount() {
        int accommodationCountPending = this.accommodationService.getPendingApprovalAccommodationCount();
        int accommodationCountReview = this.accommodationService.getUnderReviewAccommodationCount();
        AccommodationApprovalReviewCountDto accommodations
                = new AccommodationApprovalReviewCountDto(accommodationCountPending, accommodationCountReview);

        int destinationCountPending = this.destinationService.getPendingApprovalDestinationCount();
        int destinationCountReview = this.destinationService.getUnderReviewDestinationCount();
        DestinationApprovalReviewCountDto destinations
                = new DestinationApprovalReviewCountDto(destinationCountPending, destinationCountReview);
        // TODO: Refactor show only items with with trailStatus PENDING
        int trailCountPending = this.hikingTrailService.getPendingApprovalTrailCount();
        int trailCountReview = this.hikingTrailService.getUnderReviewTrailCount();
        TrailApprovalReviewCountDto trails
                = new TrailApprovalReviewCountDto(trailCountPending, trailCountReview);

        EntitiesForApprovalUnderReviewCountDto countDto =
                new EntitiesForApprovalUnderReviewCountDto(
                        accommodations,
                        destinations,
                        trails
                );

        return ResponseEntity.ok(countDto);
    }

    /*TODO: old: /waiting-approval/trails new: /trails/waiting-approval*/
    @GetMapping("/trails/waiting-approval")
    public ResponseEntity<Page<HikingTrailForApprovalProjection>> waitingForApprovalTrails(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<HikingTrailForApprovalProjection> forApproval =
                this.hikingTrailService
                        .getAllHikingTrailsForApproval(SuperUserReviewStatusEnum.PENDING, pageable);

        return ResponseEntity.ok(forApproval);
    }

    /*TODO: old: /review/trail/{id} new: /trails/{id}/review  returns details, images, gpx file info*/
    //Add data ???
    @Transactional
    @GetMapping("/trails/{id}/review")
    public ResponseEntity<ApiResponse<HikingTrailReviewDto>> reviewTrail(
            @PathVariable("id") Long trailId,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        HikingTrailReviewDto toReview =
                this.superUserService.reviewTrail(trailId, exploreBgUserDetails, SuperUserReviewStatusEnum.PENDING);

        ApiResponse<HikingTrailReviewDto> response = new ApiResponse<>(toReview);

        return ResponseEntity.ok(response);
    }

    /*TODO: old: /review/trail/{id}/claim new:  IVO: only messages, no errors
    url: /trails/{id}/claim
    */
    @PatchMapping("/trails/{id}/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleTrailReviewClaim(
            @PathVariable("id") Long trailId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.superUserService.toggleTrailReviewClaim(trailId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    /*TODO: tell Ivo old: /approve/trails/{id}   new: /trails/{id}/approve*/
    @Transactional
    @PatchMapping("/trails/{id}/approve")
    public ResponseEntity<ApiResponse<Boolean>> approveTrail(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailCreateOrReviewDto trailCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        boolean approved =
                this.superUserService.approveTrail(trailId, trailCreateOrReviewDto, exploreBgUserDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(approved);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("trails/{id}/images/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleTrailImagesClaim(
            @PathVariable("id") Long trailId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.superUserService.toggleTrailImageClaim(trailId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/trails/{id}/images/approve")
    public ResponseEntity<ApiResponse<Boolean>> approveTrailImagesClaim(
            @PathVariable("id") Long trailId,
            @RequestBody ImageApproveDto imageApproveDto,    /*TODO validate that imageApproveDto doesnt contain empty array*/
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        boolean approved =
                this.superUserService.approveTrailImages(trailId, imageApproveDto, userDetails, TRAILFOLDER);

        ApiResponse<Boolean> response = new ApiResponse<>(approved);

        return ResponseEntity.ok(response);
    }
}
