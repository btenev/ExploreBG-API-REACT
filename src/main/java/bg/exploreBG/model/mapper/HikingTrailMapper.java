package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailDetailsLikeDto;
import bg.exploreBG.model.dto.hikingTrail.HikingTrailReviewDto;
import bg.exploreBG.model.dto.hikingTrail.validate.HikingTrailCreateOrReviewDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainStatusDto;
import bg.exploreBG.model.entity.HikingTrailEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface HikingTrailMapper {

    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(trail.getModificationDate(), trail.getCreationDate()))")
    @Mapping(target = "images", expression = "java(mapImageEntityToImageIdUrlIsMainDto(trail.getImages(), trail))")
    HikingTrailDetailsDto hikingTrailEntityToHikingTrailDetailsDto(HikingTrailEntity trail);

    @Mapping(target = "id", source = "trail.id")
    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(trail.getModificationDate(), trail.getCreationDate()))")
    @Mapping(target = "images", expression = "java(mapImageEntityToImageIdUrlIsMainDto(trail.getImages(), trail))")
    @Mapping(target = "likedByUser", expression = "java(trailIsLikedByUser(trail.getLikedByUsers(), user))")
    HikingTrailDetailsLikeDto hikingTrailEntityToHikingTrailDetailsLikeDto(HikingTrailEntity trail, UserEntity user);

    HikingTrailReviewDto hikingTrailEntityToHikingTrailReviewDto(HikingTrailEntity trail);

    @Mapping(target = "destinations", ignore = true)
    @Mapping(target = "availableHuts", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "gpxFile", ignore = true)
    @Mapping(target = "mainImage", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "likedByUsers", ignore = true)
    @Mapping(target = "maxNumberOfImages", ignore = true)
    @Mapping(target = "singleComment", ignore = true)
    @Mapping(target = "hikes", ignore = true)
    HikingTrailEntity hikingTrailCreateDtoToHikingTrailEntity(HikingTrailCreateOrReviewDto hikingTrailCreateOrReviewDto);

    default List<ImageIdUrlIsMainStatusDto> mapImageEntityToImageIdUrlIsMainDto(
            List<ImageEntity> images,
            HikingTrailEntity trail
    ) {
        ImageEntity mainImage = trail.getMainImage();

        return images
                .stream()
                .map(i -> {
                    boolean isMain = Objects.equals(i.getId(), mainImage.getId());
                    return new ImageIdUrlIsMainStatusDto(i.getId(), i.getImageUrl(), isMain, i.getStatus());
                })
                .collect(Collectors.toList());
    }

    default Boolean trailIsLikedByUser(Set<UserEntity> likedByUsers, UserEntity user) {
        return likedByUsers.contains(user);
    }

    default LocalDateTime getLastUpdateDate(LocalDateTime modificationDate, LocalDateTime creationDate) {
        if (modificationDate == null) {
            return creationDate;
        }
        return modificationDate.isAfter(creationDate) ? modificationDate : creationDate;
    }
}
