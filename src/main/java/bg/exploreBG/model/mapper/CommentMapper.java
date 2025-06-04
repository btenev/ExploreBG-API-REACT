package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(comment.getModificationDate(), comment.getCreationDate()))")
    List<CommentDto> commentEntityListToCommentDtoList(List<CommentEntity> comments);

    @Mapping(target = "lastUpdateDate", expression = "java(getLastUpdateDate(comment.getModificationDate(), comment.getCreationDate()))")
    CommentDto commentEntityToCommentDto(CommentEntity comment);

    default LocalDateTime getLastUpdateDate(LocalDateTime modificationDate, LocalDateTime creationDate) {
        if (modificationDate == null) {
            return creationDate;
        }

        return modificationDate.isAfter(creationDate) ? modificationDate : creationDate;
    }
}
