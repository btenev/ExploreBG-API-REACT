package bg.exploreBG.commentableEntity;

import bg.exploreBG.model.entity.CommentEntity;

import java.util.List;

public interface CommentableEntity {
    List<CommentEntity> getComments();
    void setSingleComment(CommentEntity comment);
}
