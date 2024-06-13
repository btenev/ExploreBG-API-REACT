package bg.exploreBG.service;

import bg.exploreBG.utils.RandomUtil;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailBasicDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.mapper.HikingTrailMapper;
import bg.exploreBG.repository.HikingTrailRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HikingTrailService {

    private final HikingTrailRepository hikingTrailRepository;
    private final HikingTrailMapper hikingTrailMapper;

    public HikingTrailService(HikingTrailRepository hikingTrailRepository, HikingTrailMapper hikingTrailMapper) {
        this.hikingTrailRepository = hikingTrailRepository;
        this.hikingTrailMapper = hikingTrailMapper;
    }

    public List<HikingTrailBasicDto> getRandomNumOfHikingTrails(int limit) {
        long countOfAvailableHikingTrails = this.hikingTrailRepository.count();
        // TODO: implement error logic if no hikingTrails are available

        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfAvailableHikingTrails);

        return this.hikingTrailRepository
                .findByIdIn(randomIds);
    }

    public HikingTrailDetailsDto getHikingTrail(Long id) {
        Optional<HikingTrailEntity> trailById = this.hikingTrailRepository.findById(id);

        if (trailById.isEmpty()) {
            //TODO: implement error logic
        }

        return this.hikingTrailMapper.hikingTrailEntityToHikingTrailDetailsDto(trailById.get());
    }

    public Page<HikingTrailBasicDto> getAllHikingTrails(Pageable pageable) {
        return this.hikingTrailRepository
                .findAll(pageable)
                .map(this.hikingTrailMapper::hikingTrailEntityToHikingTrailBasicDto);
    }
}
