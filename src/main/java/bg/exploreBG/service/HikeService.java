package bg.exploreBG.service;

import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
import bg.exploreBG.model.dto.hike.HikeBasicDto;
import bg.exploreBG.model.dto.hike.HikeDetailsDto;
import bg.exploreBG.model.dto.hike.single.HikeIdDto;
import bg.exploreBG.model.dto.hike.validate.HikeCreateDto;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.HikeEntity;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.mapper.HikeMapper;
import bg.exploreBG.querybuilder.HikeQueryBuilder;
import bg.exploreBG.querybuilder.HikingTrailQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
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
    private final GenericPersistenceService<CommentEntity> commentPersistence;
    private final HikeQueryBuilder hikeQueryBuilder;
    private final HikingTrailQueryBuilder hikingTrailQueryBuilder;
    private final UserQueryBuilder userQueryBuilder;
    private final CommentService commentService;

    public HikeService(
            HikeMapper hikeMapper,
            GenericPersistenceService<HikeEntity> hikePersistence,
            GenericPersistenceService<CommentEntity> commentPersistence,
            HikeQueryBuilder hikeQueryBuilder,
            HikingTrailQueryBuilder hikingTrailQueryBuilder,
            UserQueryBuilder userQueryBuilder,
            CommentService commentService
    ) {
        this.hikeMapper = hikeMapper;
        this.hikePersistence = hikePersistence;
        this.commentPersistence = commentPersistence;
        this.hikeQueryBuilder = hikeQueryBuilder;
        this.hikingTrailQueryBuilder = hikingTrailQueryBuilder;
        this.userQueryBuilder = userQueryBuilder;
        this.commentService = commentService;
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
        newHike.setCreatedBy(verifiedUser);

        if (hikeCreateDto.trailId() != null) {
            HikingTrailEntity hikingTrailEntity =
                    this.hikingTrailQueryBuilder.getHikingTrailByIdAndStatus((hikeCreateDto.trailId()), StatusEnum.APPROVED);
            newHike.setHikingTrail(hikingTrailEntity);
        }

        logger.debug("{}", newHike);

        HikeEntity saved = this.hikePersistence.saveEntityWithReturn(newHike);
        return new HikeIdDto(saved.getId());
    }

    public CommentDto addNewHikeComment(
            Long hikeId,
            CommentRequestDto commentDto,
            UserDetails userDetails
    ) {
        return this.commentService.addComment(
                hikeId,
                commentDto,
                userDetails,
                (id, ignored) -> this.hikeQueryBuilder.getHikeWithCommentsById(id),
                this.hikePersistence::saveEntityWithoutReturn);
    }

    public void deleteHikeComment(
            Long hikeId,
            Long commentId,
            UserDetails userDetails
    ) {
        this.commentService.deleteComment(
                hikeId,
                commentId,
                userDetails,
                this.hikeQueryBuilder::getHikeWithCommentsById,
                this.hikePersistence::saveEntityWithoutReturn,
                () -> this.commentPersistence.deleteEntityWithoutReturnById(commentId));
    }
}
