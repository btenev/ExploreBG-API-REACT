package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.EntitiesPendingApprovalCountDto;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationForApprovalProjection;
import bg.exploreBG.model.dto.accommodation.AccommodationReviewDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateOrReviewDto;
import bg.exploreBG.model.dto.destination.DestinationForApprovalProjection;
import bg.exploreBG.model.dto.destination.DestinationReviewDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.dto.gpxFile.validate.GpxApproveDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailForApprovalProjection;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.single.EntitySuperUserReviewStatusDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.dto.user.UserClassDataDto;
import bg.exploreBG.model.dto.user.UserDataDto;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.dto.user.validate.UserAccountLockUnlockDto;
import bg.exploreBG.model.dto.user.validate.UserModRoleDto;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.service.SuperUserService;
import bg.exploreBG.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-users")
public class SuperUserController {
    private static final String TRAIL_FOLDER = "Trails";
    private static final String ACCOMMODATION_FOLDER = "Accommodations";
    private static final String DESTINATION_FOLDER = "Destinations";
    private final UserService userService;
    private final SuperUserService superUserService;

    public SuperUserController(
            UserService userService,
            SuperUserService superUserService
    ) {
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

    @GetMapping("/entities/waiting-approval/count")
    public ResponseEntity<EntitiesPendingApprovalCountDto> getPendingApprovalEntitiesCount() {

        EntitiesPendingApprovalCountDto entitiesCount = this.superUserService.getPendingApprovalEntitiesCount();

        return ResponseEntity.ok(entitiesCount);
    }

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
                this.superUserService.getAllHikingTrailsForApproval(pageable);

        return ResponseEntity.ok(forApproval);
    }

    @GetMapping("/accommodations/waiting-approval")
    public ResponseEntity<Page<AccommodationForApprovalProjection>> waitingForApprovalAccommodations(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<AccommodationForApprovalProjection> forApproval =
                this.superUserService.getAllAccommodationForApproval(pageable);

        return ResponseEntity.ok(forApproval);
    }

    @GetMapping("/destinations/waiting-approval")
    public ResponseEntity<Page<DestinationForApprovalProjection>> waitingForApprovalDestinations(
            @RequestParam(value = "pageNumber", defaultValue = "1", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "ASC", required = false) String sortDir
    ) {
        Sort parameters = Sort.by(Sort.Direction.valueOf(sortDir), sortBy);
        int currentPage = Math.max(pageNumber - 1, 0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, parameters);

        Page<DestinationForApprovalProjection> forApproval =
                this.superUserService.getAllDestinationForApproval(pageable);

        return ResponseEntity.ok(forApproval);
    }

    @GetMapping("/trails/{id}/reviewer")
    public ResponseEntity<UserIdDto> getHikingTrailReviewer(
            @PathVariable("id") Long trailId
    ) {
        UserIdDto reviewerId = this.superUserService.getReviewerIdByTrailId(trailId);

        return ResponseEntity.ok(reviewerId);
    }

    @GetMapping("/images/{id}/reviewer")
    public ResponseEntity<UserIdDto> getImageReviewer(
            @PathVariable("id") Long imageId
    ) {
        UserIdDto reviewerId = this.superUserService.getReviewerIdByImageId(imageId);

        return ResponseEntity.ok(reviewerId);
    }

    @GetMapping("/gpx/{id}/reviewer")
    public ResponseEntity<UserIdDto> getGpxReviewer(
            @PathVariable("id") Long gpxId
    ) {
        UserIdDto reviewerId = this.superUserService.getReviewerIdByGpxId(gpxId);

        return ResponseEntity.ok(reviewerId);
    }

    //Add data ???
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    @GetMapping("/accommodations/{id}/review")
    public ResponseEntity<ApiResponse<AccommodationReviewDto>> reviewAccommodation(
            @PathVariable("id") Long accommodationId,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        AccommodationReviewDto toReview =
                this.superUserService
                        .reviewAccommodation(accommodationId, exploreBgUserDetails, SuperUserReviewStatusEnum.PENDING);

        ApiResponse<AccommodationReviewDto> response = new ApiResponse<>(toReview);

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    @GetMapping("/destinations/{id}/review")
    public ResponseEntity<ApiResponse<DestinationReviewDto>> reviewDestination(
            @PathVariable("id") Long destinationId,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        DestinationReviewDto toReview =
                this.superUserService
                        .reviewDestination(destinationId, exploreBgUserDetails, SuperUserReviewStatusEnum.PENDING);

        ApiResponse<DestinationReviewDto> response = new ApiResponse<>(toReview);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/trails/{id}/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleTrailReviewClaim(
            @PathVariable("id") Long trailId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.superUserService.toggleTrailClaim(trailId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/accommodations/{id}/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleAccommodationClaim(
            @PathVariable("id") Long accommodationId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success =
                this.superUserService.toggleAccommodationClaim(accommodationId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/destinations/{id}/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleDestinationClaim(
            @PathVariable("id") Long destinationId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.superUserService.toggleDestinationClaim(destinationId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PatchMapping("/trails/{id}/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveTrail(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody HikingTrailCreateOrReviewDto trailCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        SuperUserReviewStatusEnum trailStatus =
                this.superUserService.approveTrail(trailId, trailCreateOrReviewDto, exploreBgUserDetails);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(trailStatus));
    }

    @PatchMapping("/accommodations/{id}/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveAccommodation(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody AccommodationCreateOrReviewDto accommodationCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
        SuperUserReviewStatusEnum accommodationStatus =
                this.superUserService
                        .approveAccommodation(accommodationId, accommodationCreateOrReviewDto, exploreBgUserDetails);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(accommodationStatus));
    }

    @PatchMapping("/destinations/{id}/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveDestination(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody DestinationCreateOrReviewDto destinationCreateOrReviewDto,
            @AuthenticationPrincipal ExploreBgUserDetails exploreBgUserDetails
    ) {
      SuperUserReviewStatusEnum destinationStatus =
              this.superUserService
                      .approveDestination(destinationId, destinationCreateOrReviewDto, exploreBgUserDetails);

      return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(destinationStatus));
    }

    @PatchMapping("/trails/{id}/images/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleTrailImagesClaim(
            @PathVariable("id") Long trailId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.superUserService.toggleTrailImageClaim(trailId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/accommodations/{id}/images/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleAccommodationImagesClaim(
            @PathVariable("id") Long accommodationId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success =
                this.superUserService.toggleAccommodationImageClaim(accommodationId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/destinations/{id}/images/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleDestinationImagesClaim(
            @PathVariable("id") Long destinationId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success =
                this.superUserService.toggleDestinationImageClaim(destinationId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/trails/{id}/images/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveTrailImagesClaim(
            @PathVariable("id") Long trailId,
            @Valid @RequestBody ImageApproveDto imageApproveDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuperUserReviewStatusEnum trailStatus =
                this.superUserService.approveTrailImages(trailId, imageApproveDto, userDetails, TRAIL_FOLDER);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(trailStatus));
    }

    @PatchMapping("/accommodations/{id}/images/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveAccommodationImagesClaim(
            @PathVariable("id") Long accommodationId,
            @Valid @RequestBody ImageApproveDto imageApproveDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuperUserReviewStatusEnum accommodationStatus =
                this.superUserService
                        .approveAccommodationImages(accommodationId, imageApproveDto, userDetails, ACCOMMODATION_FOLDER);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(accommodationStatus));
    }

    @PatchMapping("/destinations/{id}/images/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveDestinationImagesClaim(
            @PathVariable("id") Long destinationId,
            @Valid @RequestBody ImageApproveDto imageApproveDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        SuperUserReviewStatusEnum destinationStatus =
                this.superUserService
                        .approveDestinationImages(destinationId, imageApproveDto, userDetails, DESTINATION_FOLDER);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(destinationStatus));
    }

    @PatchMapping("/trails/{id}/gpx-file/claim")
    public ResponseEntity<ApiResponse<Boolean>> toggleTrailGpxFileClaim(
            @PathVariable("id") Long trailId,
            @RequestBody ReviewBooleanDto reviewBooleanDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        boolean success = this.superUserService.toggleTrailGpxFileClaim(trailId, reviewBooleanDto, userDetails);

        ApiResponse<Boolean> response = new ApiResponse<>(success);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/trails/{id}/gpx-file/approve")
    public ResponseEntity<EntitySuperUserReviewStatusDto> approveTrailGpxFileClaim(
            @PathVariable("id") Long trailId,
            @RequestBody GpxApproveDto gpxApproveDto,
            @AuthenticationPrincipal ExploreBgUserDetails userDetails
    ) {
        SuperUserReviewStatusEnum trailStatus =
                this.superUserService.approveTrailGpxFile(trailId, gpxApproveDto, userDetails);

        return ResponseEntity.ok(new EntitySuperUserReviewStatusDto(trailStatus));
    }
}
