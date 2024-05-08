package bg.exploreBG.service;

import bg.exploreBG.Util.RandomUtil;
import bg.exploreBG.model.dto.HikeBasicDto;
import bg.exploreBG.model.dto.HikeDetailsDto;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.model.mapper.HikeMapper;
import bg.exploreBG.repository.HikeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HikeService {

    private final HikeRepository hikeRepository;
    private final HikeMapper hikeMapper;
    public HikeService(HikeRepository hikeRepository, HikeMapper hikeMapper) {
        this.hikeRepository = hikeRepository;
        this.hikeMapper = hikeMapper;
    }

    public List<HikeBasicDto> getRandomNumOfHikes(int limit) {
        long countOfAvailableHikes = this.hikeRepository.count();
        // TODO: implement error logic if no hikes are available
        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfAvailableHikes);

        List<HikeEntity> hikeEntities = this.hikeRepository
                .findByIdIn(randomIds);

        return hikeEntities
                .stream()
                .map(this.hikeMapper::hikeEntityToHikeBasicDto)
                .toList();
    }

    public HikeDetailsDto getHike(Long id) {
        Optional<HikeEntity> hikeById = this.hikeRepository.findById(id);

        if (hikeById.isEmpty()) {
            // TODO: implement error logic

        }

        return this.hikeMapper.hikeEntityToHikeDetailsDto(hikeById.get());
    }

    public Page<HikeBasicDto> getAllHikes(Pageable pageable) {
        return this.hikeRepository
                .findAll(pageable)
                .map(this.hikeMapper::hikeEntityToHikeBasicDto);
    }
}
