package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.accommodation.AccommodationDetailsDto;
import bg.exploreBG.model.dto.accommodation.AccommodationDetailsWithLikesDto;
import bg.exploreBG.model.dto.accommodation.validate.AccommodationCreateDto;
import bg.exploreBG.model.dto.image.ImageIdUrlIsMainStatusDto;
import bg.exploreBG.model.entity.AccommodationEntity;
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
public interface AccommodationMapper {

    @Mapping(target = "images", expression = "java(mapImageEntityToImageIdUrlIsMainDto(accommodation.getImages(), accommodation))")
    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(accommodation.getModificationDate(), accommodation.getCreationDate()))")
    AccommodationDetailsDto accommodationEntityToAccommodationDetailsDto(AccommodationEntity accommodation);

    @Mapping(target = "id", source = "accommodation.id")
    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(accommodation.getModificationDate(), accommodation.getCreationDate()))")
    @Mapping(target = "images", expression = "java(mapImageEntityToImageIdUrlIsMainDto(accommodation.getImages(), accommodation))")
    @Mapping(target = "likedByUser", expression = "java(accommodationIsLikedByUser(accommodation.getLikedByUsers(), user))")
    AccommodationDetailsWithLikesDto accommodationEntityToAccommodationWithLikesDto(
            AccommodationEntity accommodation, UserEntity user);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "reviewedBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "accommodationStatus", ignore = true)
    @Mapping(target = "mainImage", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "likedByUsers", ignore = true)
    @Mapping(target = "maxNumberOfImages", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "singleComment", ignore = true)
    AccommodationEntity accommodationCreateDtoToAccommodationEntity(AccommodationCreateDto accommodationCreateDto);

    default List<ImageIdUrlIsMainStatusDto> mapImageEntityToImageIdUrlIsMainDto(
            List<ImageEntity> images,
            AccommodationEntity accommodation
    ) {
        ImageEntity mainImage = accommodation.getMainImage();

        return images
                .stream()
                .map(i -> {
                    boolean isMain = Objects.equals(i.getId(), mainImage.getId());
                    return new ImageIdUrlIsMainStatusDto(i.getId(), i.getImageUrl(), isMain, i.getStatus());
                })
                .collect(Collectors.toList());
    }

    default Boolean accommodationIsLikedByUser(Set<UserEntity> likedByUsers, UserEntity user) {
        return likedByUsers.contains(user);
    }

    default LocalDateTime getLastUpdateDate(LocalDateTime modificationDate, LocalDateTime creationDate) {
        if (modificationDate == null) {
            return creationDate;
        }
        return modificationDate.isAfter(creationDate) ? modificationDate : creationDate;
    }
}
