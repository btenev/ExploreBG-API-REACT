package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.dto.accommodation.single.AccommodationIdDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.repository.AccommodationRepository;
import bg.exploreBG.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccommodationService {

    private static final Logger logger = LoggerFactory.getLogger(AccommodationService.class);
    private final UserService userService;
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper mapper;

    public AccommodationService(
            UserService userService,
            AccommodationRepository accommodationRepository,
            AccommodationMapper mapper
    ) {
        this.userService = userService;
        this.accommodationRepository = accommodationRepository;
        this.mapper = mapper;
    }

    public List<AccommodationBasicPlusImageDto> getRandomNumOfAccommodations(int limit) {
        long countOfAvailableAccommodations = this.accommodationRepository.count();
        // TODO: implement error logic if no accommodations are available

        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfAvailableAccommodations);

        return this.accommodationRepository
                .findByIdIn(randomIds);
    }

    public AccommodationDetailsDto getAccommodation(Long id) {
        Optional<AccommodationEntity> accommodationById = this.accommodationRepository.findById(id);

        if (accommodationById.isEmpty()) {
            // TODO: implement error logic
        }

        return this.mapper.accommodationEntityToAccommodationDetailsDto(accommodationById.get());
    }

    public Page<AccommodationBasicPlusImageDto> getAllAccommodations(Pageable pageable) {
        return this.accommodationRepository
                .findAllBy(pageable);
    }

    public List<AccommodationBasicDto> selectAll() {
        return this.accommodationRepository
                .findAllByAccommodationStatus(StatusEnum.APPROVED);
    }

    public List<AccommodationEntity> getAccommodationsById(List<Long> ids) {
        return  this.accommodationRepository.findAllByIdInAndAccommodationStatus(ids, StatusEnum.APPROVED);
    }

    public AccommodationIdDto createAccommodation(
            Long id,
            AccommodationCreateDto accommodationCreateDto,
            UserDetails userDetails
    ) {
        UserEntity verifiedUser = this.userService.verifiedUser(id, userDetails);

        AccommodationEntity newAccommodation =
                this.mapper.accommodationCreateDtoToAccommodationEntity(accommodationCreateDto);
        newAccommodation.setOwner(verifiedUser);
        newAccommodation.setAccommodationStatus(StatusEnum.PENDING);

        logger.debug("{}", newAccommodation);

        AccommodationEntity saved = this.accommodationRepository.save(newAccommodation);
        return new AccommodationIdDto(saved.getId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getPendingApprovalAccommodationCount() {
        return this.accommodationRepository
                .countAccommodationEntitiesByAccommodationStatus(StatusEnum.PENDING);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public int getUnderReviewAccommodationCount() {
        return this.accommodationRepository
                .countAccommodationEntitiesByAccommodationStatus(StatusEnum.REVIEW);
    }
}
