package bg.exploreBG.model.mapper;

import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.entity.CommentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {
    CommentDto commentEntityToCommentDto(CommentEntity commentEntity);
}
