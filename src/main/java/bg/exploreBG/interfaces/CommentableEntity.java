package bg.exploreBG.interfaces;

import bg.exploreBG.model.entity.CommentEntity;

import java.util.List;

public interface CommentableEntity {
    List<CommentEntity> getComments();
    void setSingleComment(CommentEntity comment);
}
