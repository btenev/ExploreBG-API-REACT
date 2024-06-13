package bg.exploreBG.service;

import bg.exploreBG.model.dto.destination.DestinationBasicPlusDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.mapper.DestinationMapper;
import bg.exploreBG.repository.DestinationRepository;
import bg.exploreBG.utils.RandomUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DestinationService {
    private final DestinationRepository destinationRepository;
    private final DestinationMapper destinationMapper;

    public DestinationService(DestinationRepository destinationRepository, DestinationMapper destinationMapper) {
        this.destinationRepository = destinationRepository;
        this.destinationMapper = destinationMapper;
    }

    public List<DestinationBasicPlusDto> getRandomNumOfDestinations(int limit) {
        long countOfDestinations = this.destinationRepository.count();
        // TODO: implement error logic if no destinations are available
        // TODO: return all destinations if count <= limit
        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfDestinations);

        return this.destinationRepository
                .findByIdIn(randomIds);
    }

    public DestinationDetailsDto getDestination(Long id) {
        Optional<DestinationEntity> byId = this.destinationRepository.findById(id);
        if (byId.isEmpty()) {
            // TODO: return error message
        }

        return this.destinationMapper.destinationEntityToDestinationDetailsDto(byId.get());
    }

    public Page<DestinationBasicPlusDto> getAllDestinations(Pageable pageable) {
        return this.destinationRepository
                .findAllBy(pageable);
    }
}
