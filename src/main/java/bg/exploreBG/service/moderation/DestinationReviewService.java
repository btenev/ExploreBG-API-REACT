package bg.exploreBG.service.moderation;

import bg.exploreBG.model.dto.destination.DestinationForApprovalProjection;
import bg.exploreBG.model.dto.destination.DestinationReviewDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.dto.image.validate.ImageApproveDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.enums.SuperUserReviewStatusEnum;
import bg.exploreBG.model.mapper.DestinationMapper;
import bg.exploreBG.model.user.ExploreBgUserDetails;
import bg.exploreBG.querybuilder.DestinationQueryBuilder;
import bg.exploreBG.service.GenericPersistenceService;
import bg.exploreBG.service.moderation.common.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class DestinationReviewService {
    private final ReviewService reviewService;
    private final DestinationQueryBuilder destinationQueryBuilder;
    private final GenericPersistenceService<DestinationEntity> destinationPersistence;
    private final DestinationMapper destinationMapper;

    public DestinationReviewService(
            ReviewService reviewService,
            DestinationQueryBuilder destinationQueryBuilder,
            GenericPersistenceService<DestinationEntity> destinationPersistence,
            DestinationMapper destinationMapper
    ) {
        this.reviewService = reviewService;
        this.destinationQueryBuilder = destinationQueryBuilder;
        this.destinationPersistence = destinationPersistence;
        this.destinationMapper = destinationMapper;
    }

    public Page<DestinationForApprovalProjection> getAllDestinationForApproval(Pageable pageable) {
        return this.destinationQueryBuilder
                .getAllDestinationsByDestinationStatus(SuperUserReviewStatusEnum.PENDING, pageable);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public DestinationReviewDto reviewDestination(
            Long destinationId,
            SuperUserReviewStatusEnum supeStatus
    ) {
        DestinationEntity current =
                this.destinationQueryBuilder
                        .getDestinationByIdAndDestinationStatus(destinationId, supeStatus);

        return this.destinationMapper.destinationEntityToDestinationReviewDto(current);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void claimDestination(
            Long destinationId,
            UserDetails userDetails
    ) {
        this.reviewService
                .toggleClaim(
                        destinationId,
                        true,
                        userDetails,
                        this.destinationQueryBuilder::getDestinationEntityById,
                        this.destinationPersistence::saveEntityWithoutReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unclaimDestination(
            Long destinationId,
            UserDetails userDetails
    ) {
        this.reviewService
                .toggleClaim(
                        destinationId,
                        false,
                        userDetails,
                        this.destinationQueryBuilder::getDestinationEntityById,
                        this.destinationPersistence::saveEntityWithoutReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveDestination(
            Long destinationId,
            DestinationCreateOrReviewDto destinationCreateOrReview,
            ExploreBgUserDetails userDetails
    ) {
        return this.reviewService.approveEntity(
                destinationId,
                this.destinationQueryBuilder::getDestinationEntityById,
                destinationCreateOrReview,
                userDetails,
                this.destinationPersistence::saveEntityWithReturn);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void claimDestinationImages(
            Long destinationId,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                destinationId,
                this.destinationQueryBuilder::getDestinationWithImagesAndImageReviewerById,
                true,
                userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public void unclaimDestinationImages(
            Long destinationId,
            UserDetails userDetails
    ) {
        this.reviewService.toggleImageClaimAndSave(
                destinationId,
                this.destinationQueryBuilder::getDestinationWithImagesAndImageReviewerById,
                false,
                userDetails);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public SuperUserReviewStatusEnum approveDestinationImages(
            Long destinationId,
            ImageApproveDto imageApprove,
            UserDetails userDetails,
            String folder
    ) {
        return this.reviewService.approveEntityImages(
                destinationId,
                this.destinationQueryBuilder::getDestinationWithImagesAndImageReviewerById,
                imageApprove,
                userDetails,
                folder,
                this.destinationPersistence::saveEntityWithReturn);
    }
}
