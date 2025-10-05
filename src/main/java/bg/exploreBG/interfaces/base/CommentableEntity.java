package bg.exploreBG.interfaces.base;

import bg.exploreBG.model.entity.CommentEntity;

import java.util.List;

public interface CommentableEntity {
    List<CommentEntity> getComments();
    void setSingleComment(CommentEntity comment);
}
