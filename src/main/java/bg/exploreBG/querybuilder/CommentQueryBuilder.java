package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CommentQueryBuilder {
    private final CommentRepository repository;

    public CommentQueryBuilder(CommentRepository repository) {
        this.repository = repository;
    }

    public void validateCommentOwnership(Long commentId, String email) {
        boolean isOwner = this.repository.isUserOwnerOfComment(commentId, email);

        if (!isOwner) {
           throw commentNotFoundOrNotOwner();
        }
    }

    public CommentEntity getCommentEntityByIdIfOwner(Long commentId, String email) {
        return this.repository.findByIdAndOwnerEmail(commentId, email)
                .orElseThrow(this::commentNotFoundOrNotOwner);
    }

    private AppException commentNotFoundOrNotOwner() {
       return new AppException("Comment not found or is not owned by the specified user!", HttpStatus.BAD_REQUEST);
    }
}
