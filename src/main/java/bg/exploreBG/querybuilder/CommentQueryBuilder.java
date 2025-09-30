package bg.exploreBG.querybuilder;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class CommentQueryBuilder {
    private final CommentRepository repository;
    private final Logger logger = LoggerFactory.getLogger(CommentQueryBuilder.class);

    public CommentQueryBuilder(CommentRepository repository) {
        this.repository = repository;
    }

    public CommentEntity getCommentEntityByIdIfOwner(Long commentId, String email) {
        return this.repository.findByIdAndOwnerEmail(commentId, email)
                .orElseThrow(this::commentNotFoundOrNotOwner);
    }

    public void removeUserFromCommentsByEmail(Long newOwnerId, String email) {
        int rows = this.repository.removeUserEntityFromCommentsByEmail(newOwnerId, email);
        if (rows == 0) {
            this.logger.warn("No comments updated for owner email: {}", email);
        }
    }

    private AppException commentNotFoundOrNotOwner() {
       return new AppException("Comment not found or is not owned by the specified user!", HttpStatus.BAD_REQUEST);
    }
}
