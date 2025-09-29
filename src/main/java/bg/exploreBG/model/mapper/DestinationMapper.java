package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.destination.DestinationDetailsDto;
import bg.exploreBG.model.dto.destination.DestinationDetailsLikeDto;
import bg.exploreBG.model.dto.destination.DestinationReviewDto;
import bg.exploreBG.model.dto.destination.validate.DestinationCreateOrReviewDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainStatusDto;
import bg.exploreBG.model.entity.DestinationEntity;
import bg.exploreBG.model.entity.ImageEntity;
import bg.exploreBG.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CommentMapper.class})
public interface DestinationMapper {

    @Mapping(target = "images", expression = "java(mapImageEntityToImageIdUrlIsMainDto(destination.getImages(), destination))")
    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(destination.getModificationDate(), destination.getCreationDate()))")
    @Mapping(target = "latitude", expression = "java(destination.getLocation() != null ? destination.getLocation().getY() : null)")
    @Mapping(target = "longitude", expression = "java(destination.getLocation() != null ? destination.getLocation().getX() : null)")
    DestinationDetailsDto destinationEntityToDestinationDetailsDto(DestinationEntity destination);

    @Mapping(target = "id", source = "destination.id")
    @Mapping(target = "images", expression = "java(mapImageEntityToImageIdUrlIsMainDto(destination.getImages(), destination))")
    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(destination.getModificationDate(), destination.getCreationDate()))")
    @Mapping(target = "likedByUser", expression = "java(destinationIsLikedByUser(destination.getLikedByUsers(), user))")
    @Mapping(target = "latitude", expression = "java(destination.getLocation() != null ? destination.getLocation().getY() : null)")
    @Mapping(target = "longitude", expression = "java(destination.getLocation() != null ? destination.getLocation().getX() : null)")
    DestinationDetailsLikeDto destinationEntityToDestinationDetailsLikeDto(DestinationEntity destination, UserEntity user);

    @Mapping(target = "images", expression = "java(mapImageEntityToImageIdUrlIsMainDto(destination.getImages(), destination))")
    @Mapping(target = "latitude", expression = "java(destination.getLocation() != null ? destination.getLocation().getY() : null)")
    @Mapping(target = "longitude", expression = "java(destination.getLocation() != null ? destination.getLocation().getX() : null)")
    DestinationReviewDto destinationEntityToDestinationReviewDto(DestinationEntity destination);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "entityStatus", ignore = true)
    @Mapping(target = "mainImage", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "likedByUsers", ignore = true)
    @Mapping(target = "maxNumberOfImages", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "singleComment", ignore = true)
    @Mapping(target = "location", expression = "java(toPoint(dto.longitude(), dto.latitude()))")
    DestinationEntity destinationCreateDtoToDestinationEntity(DestinationCreateOrReviewDto dto);

    default List<ImageIdUrlIsMainStatusDto> mapImageEntityToImageIdUrlIsMainDto(
            List<ImageEntity> images,
            DestinationEntity destination
    ) {
        ImageEntity mainImage = destination.getMainImage();

        return images
                .stream()
                .map(i -> {
                    boolean isMain = Objects.equals(i.getId(), mainImage.getId());
                    return new ImageIdUrlIsMainStatusDto(i.getId(), i.getImageUrl(), isMain, i.getStatus());
                })
                .collect(Collectors.toList());
    }

    default Boolean destinationIsLikedByUser(Set<UserEntity> likedByUsers, UserEntity user) {
        return likedByUsers.contains(user);
    }

    default LocalDateTime getLastUpdateDate(LocalDateTime modificationDate, LocalDateTime creationDate) {
        if (modificationDate == null) {
            return creationDate;
        }
        return modificationDate.isAfter(creationDate) ? modificationDate : creationDate;
    }

    GeometryFactory GEOMETRY_FACTORY = new  GeometryFactory(new PrecisionModel(), 4326);

    @Named("toPoint")
    default Point toPoint(Double longitude, Double latitude) {

        if (longitude == null || latitude == null) {
            return null;
        }
        return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
    }
}
