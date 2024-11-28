package bg.exploreBG.service;

import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeDetailsDto;
import bg.exploreBG.model.dto.hike.single.HikeIdDto;
import bg.exploreBG.model.dto.hike.validate.HikeCreateDto;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.mapper.HikeMapper;
import bg.exploreBG.querybuilder.HikeQueryBuilder;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import bg.exploreBG.repository.HikeRepository;
import bg.exploreBG.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class HikeService {
    private final Logger logger = LoggerFactory.getLogger(HikeService.class);
    private final HikeMapper hikeMapper;
    private final GenericPersistenceService<HikeEntity> hikePersistence;
    private final HikeQueryBuilder hikeQueryBuilder;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final UserQueryBuilder userQueryBuilder;

    public HikeService(
            HikeMapper hikeMapper,
            GenericPersistenceService<HikeEntity> hikePersistence,
            HikeQueryBuilder hikeQueryBuilder,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            UserQueryBuilder userQueryBuilder
    ) {
        this.hikeMapper = hikeMapper;
        this.hikePersistence = hikePersistence;
        this.hikeQueryBuilder = hikeQueryBuilder;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.userQueryBuilder = userQueryBuilder;
    }

    public List<HikeBasicDto> getRandomNumOfHikes(int limit) {
        long countOfAvailableHikes = this.hikeQueryBuilder.getHikeCount();
        // TODO: implement error logic if no hikes are available
        Set<Long> randomIds = RandomUtil.generateUniqueRandomIds(limit, countOfAvailableHikes);

        List<HikeEntity> hikeEntities = this.hikeQueryBuilder.getHikeEntitiesByIds(randomIds);

        return hikeEntities
                .stream()
                .map(this.hikeMapper::hikeEntityToHikeBasicDto)
                .toList();
    }

    public HikeDetailsDto getHike(Long hikeId) {
        HikeEntity hikeById = this.hikeQueryBuilder.getHikeEntityById(hikeId);

        return this.hikeMapper.hikeEntityToHikeDetailsDto(hikeById);
    }

    public Page<HikeBasicDto> getAllHikes(Pageable pageable) {
        return this.hikeQueryBuilder.getAllHikeEntities(pageable).map(this.hikeMapper::hikeEntityToHikeBasicDto);
    }

    public HikeIdDto createHike(
            HikeCreateDto hikeCreateDto,
            UserDetails userDetails
    ) {
        UserEntity verifiedUser = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        HikeEntity newHike = this.hikeMapper.hikeCreateDtoToHikeEntity(hikeCreateDto);
        newHike.setOwner(verifiedUser);

        if (hikeCreateDto.hikingTrail() != null) {
            HikingTrailEntity hikingTrailEntity =
                    this.hikingTrailQueryBuilder.getHikingTrailById((hikeCreateDto.hikingTrail().id()));
            newHike.setHikingTrail(hikingTrailEntity);
        }

        logger.debug("{}", newHike);

        HikeEntity saved = this.hikePersistence.saveEntityWithReturn(newHike);
        return new HikeIdDto(saved.getId());
    }
}
