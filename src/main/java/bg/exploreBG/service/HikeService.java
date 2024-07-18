package bg.exploreBG.service;

import bg.exploreBG.model.dto.hike.single.HikeIdDto;
import bg.exploreBG.model.dto.hike.validate.HikeCreateDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.utils.RandomUtil;
import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeDetailsDto;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.model.mapper.HikeMapper;
import bg.exploreBG.repository.HikeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class HikeService {
    private final Logger logger = LoggerFactory.getLogger(HikeService.class);
    private final HikingTrailService hikingTrailService;
    private final UserService userService;
    private final HikeRepository hikeRepository;
    private final HikeMapper hikeMapper;

    public HikeService(
            HikingTrailService hikingTrailService,
            UserService userService,
            HikeRepository hikeRepository,
            HikeMapper hikeMapper
    ) {
        this.hikingTrailService = hikingTrailService;
        this.userService = userService;
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

    public HikeIdDto createHike(
            Long id,
            HikeCreateDto hikeCreateDto,
            UserDetails userDetails
    ) {
        UserEntity verifiedUser = this.userService.verifiedUser(id, userDetails);

        HikeEntity newHike = this.hikeMapper.hikeCreateDtoToHikeEntity(hikeCreateDto);
        newHike.setOwner(verifiedUser);

        if (hikeCreateDto.hikingTrail() != null) {
            HikingTrailEntity hikingTrailEntity =
                    this.hikingTrailService.hikingTrailExist(hikeCreateDto.hikingTrail().id());
            newHike.setHikingTrail(hikingTrailEntity);
        }

        logger.debug("{}", newHike);

        HikeEntity saved = this.hikeRepository.save(newHike);
        return new HikeIdDto(saved.getId());
    }
}
