package bg.exploreBG.utils;

import bg.exploreBG.model.dto.comment.CommentCreateDto;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.UserEntity;

import java.time.LocalDateTime;

public class CommentUtil {
    public static CommentEntity createNewComment(
            CommentCreateDto commentDto,
            UserEntity verifiedUser
    ) {
        CommentEntity newComment = new CommentEntity();
        newComment.setMessage(commentDto.message());
        newComment.setCreationDate(LocalDateTime.now());
        newComment.setOwner(verifiedUser);

        return newComment;
    }
}
