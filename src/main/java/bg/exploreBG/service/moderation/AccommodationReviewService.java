package bg.exploreBG.service.moderation;

import bg.exploreBG.model.dto.accommodation.AccommodationForApprovalProjection;
import bg.exploreBG.model.dto.accommodation.AccommodationReviewDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.querybuilder.AccommodationQueryBuilder;
import bg.exploreBG.service.GenericPersistenceService;
import bg.exploreBG.service.moderation.common.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AccommodationReviewService {
    private final ReviewService reviewService;
    private final AccommodationQueryBuilder accommodationQueryBuilder;
    private final GenericPersistenceService<AccommodationEntity> accommodationPersistence;
    private final AccommodationMapper accommodationMapper;

    public AccommodationReviewService(
            ReviewService reviewService,
            AccommodationQueryBuilder accommodationQueryBuilder,
            GenericPersistenceService<AccommodationEntity> accommodationPersistence,
            AccommodationMapper accommodationMapper
    ) {
        this.reviewService = reviewService;
        this.accommodationQueryBuilder = accommodationQueryBuilder;
        this.accommodationPersistence = accommodationPersistence;
        this.accommodationMapper = accommodationMapper;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public Page<AccommodationForApprovalProjection> getAllAccommodationForApproval(
            Pageable pageable
    ) {
        return this.accommodationQueryBuilder
                .getAllAccommodationsByAccommodationStatus(SuperUserReviewStatusEnum.PENDING, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public AccommodationReviewDto reviewAccommodation(
            Long accommodationId,
            SuperUserReviewStatusEnum supeStatus
    ) {
        AccommodationEntity current =
                this.accommodationQueryBuilder
                        .getAccommodationByIdAndAccommodationStatus(accommodationId, supeStatus);

        return this.accommodationMapper.accommodationEntityToAccommodationReviewDto(current);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void claimAccommodation(
            Long accommodationId,
            UserDetails userDetails
    ) {
        this.reviewService
                .toggleClaim(
                        accommodationId,
                        true,
                        userDetails,
                        this.accommodationQueryBuilder::getAccommodationEntityById,
                        this.accommodationPersistence::saveEntityWithoutReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unclaimAccommodation(
            Long accommodationId,
            UserDetails userDetails
    ) {
        this.reviewService
                .toggleClaim(
                        accommodationId,
                        false,
                        userDetails,
                        this.accommodationQueryBuilder::getAccommodationEntityById,
                        this.accommodationPersistence::saveEntityWithoutReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveAccommodation(
            Long accommodationId,
            AccommodationCreateOrReviewDto accommodationCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        return this.reviewService.approveEntity(
                accommodationId,
                this.accommodationQueryBuilder::getAccommodationEntityById,
                accommodationCreateOrReview,
                userDetails,
                this.accommodationPersistence::saveEntityWithReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void claimAccommodationImages(
            Long accommodationId,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                accommodationId,
                this.accommodationQueryBuilder::getAccommodationWithImagesAndImageReviewerById,
                true,
                userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unclaimAccommodationImages(
            Long accommodationId,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                accommodationId,
                this.accommodationQueryBuilder::getAccommodationWithImagesAndImageReviewerById,
                true,
                userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveAccommodationImages(
            Long accommodationId,
            ImageApproveDto imageApprove,
            UserDetails userDetails,
            String folder
    ) {
        return this.reviewService.approveEntityImages(
                accommodationId,
                this.accommodationQueryBuilder::getAccommodationWithImagesAndImageReviewerById,
                imageApprove,
                userDetails,
                folder,
                this.accommodationPersistence::saveEntityWithReturn);
    }
}
