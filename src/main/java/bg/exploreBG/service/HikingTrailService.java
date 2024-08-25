package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.ReviewBooleanDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.destination.DestinationBasicDto;
import bg.exploreBG.model.dto.destination.single.DestinationIdDto;
import bg.exploreBG.model.dto.hikingTrail.*;
import bg.exploreBG.model.dto.hikingTrail.single.*;
import bg.exploreBG.model.dto.hikingTrail.validate.*;
import bg.exploreBG.model.dto.user.single.UserIdDto;
import bg.exploreBG.model.entity.*;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.enums.SuitableForEnum;
import bg.exploreBG.model.mapper.CommentMapper;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.repository.HikingTrailRepository;
import bg.exploreBG.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HikingTrailService {

    private static final Logger logger = LoggerFactory.getLogger(HikingTrailService.class);
    private final HikingTrailRepository hikingTrailRepository;
    private final HikingTrailMapper hikingTrailMapper;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final DestinationService destinationService;
    private final AccommodationService accommodationService;
    private final CommentService commentService;

    public HikingTrailService(
            HikingTrailRepository hikingTrailRepository,
            HikingTrailMapper hikingTrailMapper,
            CommentMapper commentMapper,
            UserService userService,
            DestinationService destinationService,
            AccommodationService accommodationService,
            CommentService commentService
    ) {
        this.hikingTrailRepository = hikingTrailRepository;
        this.hikingTrailMapper = hikingTrailMapper;
        this.commentMapper = commentMapper;
        this.userService = userService;
        this.destinationService = destinationService;
        this.accommodationService = accommodationService;
        this.commentService = commentService;
    }

    public List<HikingTrailBasicDto> getRandomNumOfHikingTrails(int limit) {
        long countOfAvailableHikingTrails = this.hikingTrailRepository.count();
        // TODO: implement error logic if no hikingTrails are available

        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfAvailableHikingTrails);

        return this.hikingTrailRepository
                .findByIdIn(randomIds);
    }

    public HikingTrailDetailsDto getHikingTrail(Long id) {
        HikingTrailEntity trailById = hikingTrailExistAndApproved(id);

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(trailById);
    }

    public HikingTrailDetailsDto getHikingTrailAuthenticated(Long id, UserDetails userDetails) {
        String username = userDetails.getUsername();
        Optional<HikingTrailEntity> possibleOwner = this.hikingTrailRepository
                .findByIdAndStatusApprovedOrStatusPendingAndOwner(id, username);

        if (possibleOwner.isEmpty()) {
            throw new AppException("HikingTrail not found or invalid status!", HttpStatus.BAD_REQUEST);
        }

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(possibleOwner.get());
    }

    public Page<HikingTrailBasicDto> getAllHikingTrails(Pageable pageable) {
        return this.hikingTrailRepository
                .findAllByTrailStatus(StatusEnum.APPROVED, pageable);
    }

    public Long createHikingTrail(
            Long id,
            HikingTrailCreateOrReviewDto hikingTrailCreateOrReviewDto,
            UserDetails userDetails
    ) {
        UserEntity validUser = this.userService.verifiedUser(id, userDetails);

        HikingTrailEntity newHikingTrail =
                this.hikingTrailMapper
                        .hikingTrailCreateDtoToHikingTrailEntity(hikingTrailCreateOrReviewDto);

//        logger.debug("{}", newHikingTrail);

        newHikingTrail.setTrailStatus(StatusEnum.PENDING);
        newHikingTrail.setCreatedBy(validUser);
        newHikingTrail.setCreationDate(LocalDateTime.now());

        if (!hikingTrailCreateOrReviewDto.destinations().isEmpty()) {
            List<DestinationEntity> destinationEntities =
                    mapDtoToDestinationEntities(hikingTrailCreateOrReviewDto.destinations());
            newHikingTrail.setDestinations(destinationEntities);
        }

        if (!hikingTrailCreateOrReviewDto.availableHuts().isEmpty()) {
            List<AccommodationEntity> accommodationEntities
                    = mapDtoToAccommodationEntities(hikingTrailCreateOrReviewDto.availableHuts());
            newHikingTrail.setAvailableHuts(accommodationEntities);
        }

        return this.hikingTrailRepository.save(newHikingTrail).getId();
    }

    public HikingTrailStartPointDto updateHikingTrailStartPoint(
            Long id,
            HikingTrailUpdateStartPointDto hikingTrailStartPointDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !hikingTrailStartPointDto.startPoint().equals(currentTrail.getStartPoint());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setStartPoint(hikingTrailStartPointDto.startPoint());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailStartPointDto(saved.getStartPoint());
    }

    public HikingTrailEndPointDto updateHikingTrailEndPoint(
            Long id,
            HikingTrailUpdateEndPointDto hikingTrailEndPointDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !hikingTrailEndPointDto.endPoint().equals(currentTrail.getEndPoint());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setEndPoint(hikingTrailEndPointDto.endPoint());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailEndPointDto(saved.getEndPoint());
    }

    public HikingTrailTotalDistanceDto updateHikingTrailTotalDistance(
            Long id,
            HikingTrailUpdateTotalDistanceDto trailTotalDistanceDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !trailTotalDistanceDto.totalDistance().equals(currentTrail.getTotalDistance());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setTotalDistance(trailTotalDistanceDto.totalDistance());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailTotalDistanceDto(saved.getTotalDistance());
    }

    public HikingTrailElevationGainedDto updateHikingTrailElevationGained(
            Long id,
            HikingTrailUpdateElevationGainedDto elevationGainedDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !elevationGainedDto.elevationGained().equals(currentTrail.getElevationGained());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setElevationGained(elevationGainedDto.elevationGained());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailElevationGainedDto(saved.getElevationGained());
    }

    public HikingTrailWaterAvailableDto updateHikingTrailWaterAvailable(
            Long id,
            HikingTrailUpdateWaterAvailableDto hikingTrailWaterAvailableDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !hikingTrailWaterAvailableDto.waterAvailable().equals(currentTrail.getWaterAvailable());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setWaterAvailable(hikingTrailWaterAvailableDto.waterAvailable());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailWaterAvailableDto(saved.getWaterAvailable().getValue());
    }

    public HikingTrailActivityDto updateHikingTrailActivity(
            Long id,
            HikingTrailUpdateActivityDto hikingTrailActivityDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        List<SuitableForEnum> currentTrailActivity = currentTrail.getActivity();

        boolean noMatch = !hikingTrailActivityDto.activity().equals(currentTrailActivity);
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setActivity(hikingTrailActivityDto.activity());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailActivityDto(saved.getActivity().stream().map(SuitableForEnum::getValue).collect(Collectors.toList()));
    }

    public HikingTrailTrailInfoDto updateHikingTrailTrailInfo(
            Long id,
            HikingTrailUpdateTrailInfoDto trailInfoDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !trailInfoDto.trailInfo().equals(currentTrail.getTrailInfo());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setTrailInfo(trailInfoDto.trailInfo());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailTrailInfoDto(saved.getTrailInfo());
    }

    public List<AccommodationBasicDto> updateHikingTrailAvailableHuts(
            Long id,
            HikingTrailUpdateAvailableHutsDto hikingTrailAvailableHutsDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        List<AccommodationEntity> currentTrailAvailableHuts = currentTrail.getAvailableHuts();
        List<AccommodationIdDto> currentHutsDto =
                currentTrailAvailableHuts
                        .stream()
                        .map(ae -> new AccommodationIdDto(ae.getId()))
                        .toList();

        boolean noMatch = !currentHutsDto.equals(hikingTrailAvailableHutsDto.availableHuts());
        HikingTrailEntity saved;

        if (noMatch) {
            List<AccommodationEntity> newSelection =
                    mapDtoToAccommodationEntities(hikingTrailAvailableHutsDto.availableHuts());
            currentTrail.setAvailableHuts(newSelection);
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return saved
                .getAvailableHuts()
                .stream()
                .map(hut -> new AccommodationBasicDto(hut.getId(), hut.getAccommodationName()))
                .collect(Collectors.toList());
    }

    public List<DestinationBasicDto> updateHikingTrailDestinations(
            Long id,
            HikingTrailUpdateDestinationsDto hikingTrailDestinationsDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);
        List<DestinationEntity> currentTrailDestinations = currentTrail.getDestinations();
        List<DestinationIdDto> destinationIdDto =
                currentTrailDestinations
                        .stream()
                        .map(de -> new DestinationIdDto(de.getId()))
                        .toList();

        boolean noMatch = !destinationIdDto.equals(hikingTrailDestinationsDto.destinations());
        HikingTrailEntity saved;

        if (noMatch) {
            List<DestinationEntity> newSelection =
                    mapDtoToDestinationEntities(hikingTrailDestinationsDto.destinations());
            currentTrail.setDestinations(newSelection);
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return saved
                .getDestinations()
                .stream()
                .map(destination -> new DestinationBasicDto(destination.getId(), destination.getDestinationName()))
                .collect(Collectors.toList());
    }

    public HikingTrailDifficultyDto updateHikingTrailDifficulty(
            Long id,
            HikingTrailUpdateTrailDifficultyDto hikingTrailDifficultyDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = verifiedHikingTrail(id, userDetails);

        boolean noMatch = !currentTrail.getTrailDifficulty().equals(hikingTrailDifficultyDto.trailDifficulty());
        HikingTrailEntity saved;

        if (noMatch) {
            currentTrail.setTrailDifficulty(hikingTrailDifficultyDto.trailDifficulty());
            saved = this.hikingTrailRepository.save(currentTrail);
        } else {
            saved = currentTrail;
        }

        return new HikingTrailDifficultyDto(saved.getTrailDifficulty().getLevel());
    }

    public List<HikingTrailIdTrailNameDto> selectAll() {
        return this.hikingTrailRepository.findAllBy();
    }

    public CommentDto addNewTrailComment(
            Long id,
            Long trailId,
            CommentCreateDto commentDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = hikingTrailExist(trailId);
        UserEntity userCommenting = this.userService.verifiedUser(id, userDetails);

        CommentEntity savedComment = this.commentService.saveComment(commentDto, userCommenting);

        currentTrail.setSingleComment(savedComment);
        this.hikingTrailRepository.save(currentTrail);

        return this.commentMapper.commentEntityToCommentDto(savedComment);
    }

    /*
    In this example, the ParentEntity has a list of ChildEntity objects. The @OneToMany annotation with the cascade = CascadeType.ALL
    attribute means that any operation (including deletion) performed on the ParentEntity will be cascaded to the ChildEntity objects.
    The orphanRemoval = true attribute ensures that if a ChildEntity object is removed from the collection, it will be deleted from the database.

    To delete a ChildEntity, you can simply remove it from the collection in the ParentEntity and then save the ParentEntity.
    The removed ChildEntity will be deleted from the database due to the cascading delete operation.
    */
    public boolean deleteTrailComment(
            Long commentId,
            Long trailId,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = hikingTrailExist(trailId); // trail exist
        CommentEntity commentToDelete = this.commentService.verifiedComment(commentId, userDetails);

        boolean removedFromTrail = currentTrail.getComments().remove(commentToDelete);

        boolean removedFromDatabase = this.commentService.deleteComment(commentToDelete);

        return removedFromTrail && removedFromDatabase;
    }

    private HikingTrailEntity verifiedHikingTrail(Long id, UserDetails userDetails) {

        HikingTrailEntity currentTrail = hikingTrailExist(id);
        UserEntity createdBy = currentTrail.getCreatedBy();

        this.userService.verifiedUser(createdBy, userDetails); // throws exception if no match
        return currentTrail;
    }

    protected HikingTrailEntity hikingTrailExist(Long id) {
        Optional<HikingTrailEntity> trailById = this.hikingTrailRepository.findById(id);

        if (trailById.isEmpty()) {
            throw new AppException("Hiking trail not found!", HttpStatus.NOT_FOUND);
        }

        return trailById.get();
    }

    //TODO: use this method for members
    private HikingTrailEntity hikingTrailExistAndApproved(Long id) {
        Optional<HikingTrailEntity> byIdAndTrailStatus =
                this.hikingTrailRepository.findByIdAndTrailStatus(id, StatusEnum.APPROVED);

        if (byIdAndTrailStatus.isEmpty()) {
            throw new AppException("Hiking trail not found or not approved!", HttpStatus.NOT_FOUND);
        }

        return byIdAndTrailStatus.get();
    }

    protected HikingTrailEntity hikingTrailExistApprovedPendingUserOwner(Long id, String username) {

        Optional<HikingTrailEntity> exist = this.hikingTrailRepository
                .findByIdAndTrailStatusInAndCreatedByEmail(
                        id,
                        List.of(StatusEnum.PENDING, StatusEnum.APPROVED),
                        username);
        logger.info("user id " + id + "username " + username);
        if (exist.isEmpty()) {
            throw new AppException("Hiking trail not found or status is incorrect!", HttpStatus.BAD_REQUEST);
        }

        return exist.get();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    private HikingTrailEntity hikingTrailExistAndPending(Long id) {
        Optional<HikingTrailEntity> byIdAndStatusPending =
                this.hikingTrailRepository.findByIdAndTrailStatus(id, StatusEnum.PENDING);

        if (byIdAndStatusPending.isEmpty()) {
            throw new AppException("Hiking trail not found or not pending!", HttpStatus.NOT_FOUND);
        }

        return byIdAndStatusPending.get();
    }

    private List<AccommodationEntity> mapDtoToAccommodationEntities(List<AccommodationIdDto> ids) {

        List<Long> accommodationIds = ids.stream().map(AccommodationIdDto::id).toList();

        return this.accommodationService.getAccommodationsById(accommodationIds);
    }

    private List<DestinationEntity> mapDtoToDestinationEntities(List<DestinationIdDto> ids) {

        List<Long> destinationIds = ids.stream().map(DestinationIdDto::id).toList();

        return this.destinationService.getDestinationsByIds(destinationIds);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getPendingApprovalTrailCount() {
        return this.hikingTrailRepository
                .countHikingTrailEntitiesByTrailStatus(StatusEnum.PENDING);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getUnderReviewTrailCount() {
        return this.hikingTrailRepository
                .countHikingTrailEntitiesByTrailStatus(StatusEnum.REVIEW);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Page<HikingTrailForApprovalProjection> getAllHikingTrailsForApproval(
            List<StatusEnum> statuses,
            Pageable pageable
    ) {
        return this.hikingTrailRepository
                .getHikingTrailEntitiesByTrailStatusIn(statuses, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public HikingTrailReviewDto reviewTrail(Long id, ExploreBgUserDetails userDetails) {

        HikingTrailEntity currentTrail = hikingTrailExist(id);

        StatusEnum trailStatus = currentTrail.getTrailStatus();
        String reviewedBy = currentTrail.getReviewedBy() != null
                ? currentTrail.getReviewedBy().getUsername() : null;


        if (trailStatus.equals(StatusEnum.PENDING)
                || reviewedBy != null
                && trailStatus.equals(StatusEnum.REVIEW) && reviewedBy.equals(userDetails.getProfileName())
        ) {
            return this.hikingTrailMapper.hikingTrailEntityToHikingTrailReviewDto(currentTrail);
        }

        throw new AppException("Item with invalid status for review!", HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean claimTrailReview(
            Long id,
            ReviewBooleanDto reviewBooleanDto,
            UserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = hikingTrailExist(id);
        StatusEnum trailStatus = currentTrail.getTrailStatus();

        UserEntity reviewedByUser = currentTrail.getReviewedBy() != null ? currentTrail.getReviewedBy() : null;
        String reviewedByUserUsername = reviewedByUser != null ? reviewedByUser.getUsername() : null;

        UserEntity currentUser = this.userService.getUserEntity(userDetails.getUsername());

        if (reviewBooleanDto.review()) { // claim item for review

            if (reviewedByUser == null) { // no user - should be pending
                currentTrail.setTrailStatus(StatusEnum.REVIEW);
                currentTrail.setReviewedBy(currentUser);
            } else { // has user but...

                if (trailStatus.equals(StatusEnum.REVIEW) // already claimed by you
                        && reviewedByUserUsername.equals(currentUser.getUsername())) {
                    throw new AppException("You have already claimed this item for review!", HttpStatus.BAD_REQUEST);
                }

                if (trailStatus.equals(StatusEnum.REVIEW) // already claimed by another user
                        && !reviewedByUserUsername.equals(currentUser.getUsername())) {
                    throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
                }

                if (trailStatus.equals(StatusEnum.APPROVED)) { // already approved
                    throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
                }
            }
        } else { // cancel claim
            if (trailStatus.equals(StatusEnum.REVIEW) // already claimed by you
                    && reviewedByUserUsername != null
                    && reviewedByUserUsername.equals(currentUser.getUsername())) {

                currentTrail.setTrailStatus(StatusEnum.PENDING);
                currentTrail.setReviewedBy(null);
            } else {
                if (trailStatus.equals(StatusEnum.PENDING)) {
                    throw new AppException("You cannot cancel the review for an item that you haven't claimed!", HttpStatus.BAD_REQUEST);
                }

                if (trailStatus.equals(StatusEnum.REVIEW) // already claimed by another user
                        && reviewedByUserUsername != null
                        && !reviewedByUserUsername.equals(currentUser.getUsername())) {
                    throw new AppException("The item has already been claimed by another user!", HttpStatus.BAD_REQUEST);
                }

                if (trailStatus.equals(StatusEnum.APPROVED)) { // already approved
                    throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
                }
            }
        }

        this.hikingTrailRepository.save(currentTrail);
        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public boolean approveTrail(
            Long id,
            HikingTrailCreateOrReviewDto trailCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        HikingTrailEntity currentTrail = hikingTrailExist(id);
        StatusEnum trailStatus = currentTrail.getTrailStatus();
        String reviewedByUserProfile = currentTrail.getReviewedBy() != null ? currentTrail.getReviewedBy().getUsername() : null;


        if (reviewedByUserProfile == null) {
            throw new AppException("A pending item can not be approved!", HttpStatus.BAD_REQUEST);
        }

        if (trailStatus.equals(StatusEnum.REVIEW) && !reviewedByUserProfile.equals(userDetails.getProfileName())) {
            throw new AppException("The item has already been claimed by another user! You can not approved it!", HttpStatus.BAD_REQUEST);
        }

        if (trailStatus.equals(StatusEnum.APPROVED)) {
            throw new AppException("The item has already been approved!", HttpStatus.BAD_REQUEST);
        }


        if (!currentTrail.getStartPoint().equals(trailCreateOrReview.startPoint())) {
            currentTrail.setStartPoint(trailCreateOrReview.startPoint());
        }

        if (!currentTrail.getEndPoint().equals(trailCreateOrReview.endPoint())) {
            currentTrail.setEndPoint(trailCreateOrReview.endPoint());
        }
        // totalDistance == null -> new value != null
        if (currentTrail.getTotalDistance() == null && trailCreateOrReview.totalDistance() != null
                || (currentTrail.getTotalDistance() != null
                && !currentTrail.getTotalDistance().equals(trailCreateOrReview.totalDistance()))
        ) {
            currentTrail.setTotalDistance(trailCreateOrReview.totalDistance());
        }

        if (!currentTrail.getTrailInfo().equals(trailCreateOrReview.trailInfo())) {
            currentTrail.setTrailInfo(trailCreateOrReview.trailInfo());
        }

        if (!currentTrail.getSeasonVisited().equals(trailCreateOrReview.seasonVisited())) {
            currentTrail.setSeasonVisited(trailCreateOrReview.seasonVisited());
        }

        if (!currentTrail.getWaterAvailable().equals(trailCreateOrReview.waterAvailable())) {
            currentTrail.setWaterAvailable(trailCreateOrReview.waterAvailable());
        }

        if (!currentTrail.getTrailDifficulty().equals(trailCreateOrReview.trailDifficulty())) {
            currentTrail.setTrailDifficulty(trailCreateOrReview.trailDifficulty());
        }

        if (!currentTrail.getActivity().equals(trailCreateOrReview.activity())) {
            currentTrail.setActivity(trailCreateOrReview.activity());
        }

        if (currentTrail.getElevationGained() == null && trailCreateOrReview.elevationGained() != null
                || (currentTrail.getElevationGained() != null
                && !currentTrail.getElevationGained().equals(trailCreateOrReview.elevationGained()))
        ) {
            currentTrail.setElevationGained(trailCreateOrReview.elevationGained());
        }

        if (!currentTrail.getNextTo().equals(trailCreateOrReview.nextTo())) {
            currentTrail.setNextTo(trailCreateOrReview.nextTo());
        }

        List<DestinationIdDto> destinationIdDto =
                currentTrail.getDestinations()
                        .stream()
                        .map(de -> new DestinationIdDto(de.getId()))
                        .toList();

        if (!destinationIdDto.equals(trailCreateOrReview.destinations())) {
            List<DestinationEntity> destinationEntities =
                    mapDtoToDestinationEntities(trailCreateOrReview.destinations());
            currentTrail.setDestinations(destinationEntities);
        }

        List<AccommodationIdDto> accommodationIdDto =
                currentTrail.getAvailableHuts()
                        .stream().map(a -> new AccommodationIdDto(a.getId()))
                        .toList();

        if (!accommodationIdDto.equals(trailCreateOrReview.availableHuts())) {
            List<AccommodationEntity> accommodationEntities =
                    mapDtoToAccommodationEntities(trailCreateOrReview.availableHuts());
            currentTrail.setAvailableHuts(accommodationEntities);
        }

        currentTrail.setTrailStatus(StatusEnum.APPROVED);

        this.hikingTrailRepository.save(currentTrail);

        return true;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public UserIdDto getReviewerId(Long id) {
        HikingTrailEntity currentTrail = hikingTrailExist(id);
        Long reviewerId =
                currentTrail.getReviewedBy() != null
                        ? currentTrail.getReviewedBy().getId() : null;
        return new UserIdDto(reviewerId);
    }

    public HikingTrailEntity saveHikingTrailEntity(HikingTrailEntity hikingTrail) {
        return this.hikingTrailRepository.save(hikingTrail);
    }

}
