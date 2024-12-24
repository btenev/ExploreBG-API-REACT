package bg.exploreBG.service;

import bg.exploreBG.commentableEntity.CommentableEntity;
import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.comment.validate.CommentUpdateDto;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.enums.StatusEnum;
import bg.exploreBG.model.mapper.CommentMapper;
import bg.exploreBG.querybuilder.CommentQueryBuilder;
import bg.exploreBG.querybuilder.UserQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class CommentService {
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentMapper commentMapper;
    private final GenericPersistenceService<CommentEntity> commentPersistence;
    private final CommentQueryBuilder commentQueryBuilder;
    private final UserQueryBuilder userQueryBuilder;

    public CommentService(
            CommentMapper commentMapper,
            GenericPersistenceService<CommentEntity> commentPersistence,
            CommentQueryBuilder commentQueryBuilder,
            UserQueryBuilder userQueryBuilder
    ) {
        this.commentMapper = commentMapper;
        this.commentPersistence = commentPersistence;
        this.commentQueryBuilder = commentQueryBuilder;
        this.userQueryBuilder = userQueryBuilder;
    }

    public <E extends CommentableEntity> CommentDto addComment(
            Long entityId,
            StatusEnum status,
            CommentCreateDto commentDto,
            UserDetails userDetails,
            BiFunction<Long, StatusEnum, E> fetchEntityWithComments,
            Consumer<E> saveEntity
    ) {
        E currentEntity = fetchEntityWithComments.apply(entityId, status);

        UserEntity userCommenting = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        CommentEntity savedComment = saveComment(commentDto, userCommenting);

        currentEntity.setSingleComment(savedComment);
        saveEntity.accept(currentEntity);

        return this.commentMapper.commentEntityToCommentDto(savedComment);
    }

    public CommentDto updateComment(
            Long commentId,
            CommentUpdateDto commentDto,
            UserDetails userDetails
    ) {
        CommentEntity verifiedComment =
                this.commentQueryBuilder
                        .getCommentEntityByIdIfOwner(commentId, userDetails.getUsername());
        CommentEntity updated = updateCommentValues(verifiedComment, commentDto);

        CommentEntity saved = this.commentPersistence.saveEntityWithReturn(updated);

        return this.commentMapper.commentEntityToCommentDto(saved);
    }


    public <E extends CommentableEntity> void deleteComment(
            Long entityId,
            Long commentId,
            UserDetails userDetails,
            Function<Long, E> fetchEntityWithComments,
            Consumer<E> saveEntity,
            Consumer<E> deleteComment
    ) {
        E currentEntity = fetchEntityWithComments.apply(entityId);

        this.commentQueryBuilder.validateCommentOwnership(commentId, userDetails.getUsername());

        boolean commentRemoved = currentEntity.getComments().removeIf(c -> c.getId().equals(commentId));

        if (!commentRemoved) {
            throw new AppException("Comment with id " + commentId + " was not found!",
                    HttpStatus.NOT_FOUND);
        }

        saveEntity.accept(currentEntity);
        /* TODO: Handle exceptions appropriately, consider transaction management */
        deleteComment.accept(currentEntity);
    }

    private CommentEntity saveComment(CommentCreateDto commentDto, UserEntity commentUser) {
        CommentEntity newComment = createNewComment(commentDto, commentUser);
        return this.commentPersistence.saveEntityWithReturn(newComment);
    }

    private CommentEntity updateCommentValues(
            CommentEntity verifiedComment,
            CommentUpdateDto commentDto
    ) {
        verifiedComment.setMessage(commentDto.message());
        verifiedComment.setModificationDate(LocalDateTime.now());
        logger.warn("Set comment modification date {}", verifiedComment.getModificationDate());
        return verifiedComment;
    }

    private CommentEntity createNewComment(
            CommentCreateDto commentDto,
            UserEntity verifiedUser
    ) {
        CommentEntity newComment = new CommentEntity();
        newComment.setMessage(commentDto.message());
        newComment.setCreationDate(LocalDateTime.now());
        logger.warn("Set comment creation date {}", newComment.getCreationDate());
        newComment.setOwner(verifiedUser);

        return newComment;
    }
}
