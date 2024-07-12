package bg.exploreBG.service;

import bg.exploreBG.model.dto.accommodation.AccommodationBasicDto;
import bg.exploreBG.model.dto.accommodation.AccommodationBasicPlusImageDto;
import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.entity.AccommodationEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.mapper.AccommodationMapper;
import bg.exploreBG.repository.AccommodationRepository;
import bg.exploreBG.utils.RandomUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccommodationService {
    private final AccommodationRepository accommodationRepository;
    private final AccommodationMapper mapper;

    public AccommodationService(AccommodationRepository accommodationRepository, AccommodationMapper mapper) {
        this.accommodationRepository = accommodationRepository;
        this.mapper = mapper;
    }

    public List<AccommodationBasicPlusImageDto> getRandomNumOfAccommodations(int limit) {
        long countOfAvailableAccommodations = this.accommodationRepository.count();
        // TODO: implement error logic if no accommodations are available

        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit,countOfAvailableAccommodations);

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
        List<AccommodationEntity> selected = new ArrayList<>();
        for (Long id : ids) {
            Optional<AccommodationEntity> byId = this.accommodationRepository.findById(id);
            byId.ifPresent(selected::add);
        }
        return selected;
    }
}
