package bg.exploreBG.service;

import bg.exploreBG.interfaces.base.CommentableEntity;
import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentRequestDto;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class CommentService {
    @FunctionalInterface
    public interface EntityFetcher<E extends CommentableEntity> {
        E fetch(Long id, StatusEnum status);
    }

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
            CommentRequestDto requestDto,
            UserDetails userDetails,
            EntityFetcher<E> fetchEntityWithComments,
            Consumer<E> saveEntity
    ) {
        return addComment(entityId, requestDto, userDetails, fetchEntityWithComments, saveEntity, null);
    }

    public <E extends CommentableEntity> CommentDto addComment(
            Long entityId,
            CommentRequestDto requestDto,
            UserDetails userDetails,
            EntityFetcher<E> fetchEntityWithComments,
            Consumer<E> saveEntity,
            StatusEnum detailsStatus
    ) {
        E currentEntity = fetchEntityWithComments.fetch(entityId, detailsStatus);

        UserEntity userCommenting = this.userQueryBuilder.getUserEntityByEmail(userDetails.getUsername());

        CommentEntity savedComment = saveComment(requestDto, userCommenting);

        currentEntity.setSingleComment(savedComment);
        saveEntity.accept(currentEntity);

        return this.commentMapper.commentEntityToCommentDto(savedComment);
    }

    public CommentDto updateComment(
            Long commentId,
            CommentRequestDto commentDto,
            UserDetails userDetails
    ) {
        CommentEntity verifiedComment =
                this.commentQueryBuilder
                        .getCommentEntityByIdIfOwner(commentId, userDetails.getUsername());
        CommentEntity updated = updateCommentValues(verifiedComment, commentDto);

        CommentEntity saved = this.commentPersistence.saveEntityWithReturn(updated);

        return this.commentMapper.commentEntityToCommentDto(saved);
    }

    @Transactional
    public <E extends CommentableEntity> void deleteComment(
            Long entityId,
            Long commentId,
            UserDetails userDetails,
            Function<Long, E> fetchEntityWithComments,
            Consumer<E> saveEntity,
            Runnable deleteCommentAction
    ) {
        E currentEntity = fetchEntityWithComments.apply(entityId);

        boolean removed = currentEntity.getComments().removeIf(c -> {
            if (!c.getId().equals(commentId)) return false;
            if (!c.getOwner().getEmail().equals(userDetails.getUsername())) {
                throw new AppException(
                        "You do not have permission to delete this comment.",
                        HttpStatus.FORBIDDEN);
            }
            return true;
        });

        if (!removed) {
            throw new AppException(
                    "Comment with id " + commentId + " was not found on entity " + entityId,
                    HttpStatus.NOT_FOUND);
        }

        saveEntity.accept(currentEntity);
        deleteCommentAction.run();
    }

    private CommentEntity saveComment(CommentRequestDto commentDto, UserEntity commentUser) {
        CommentEntity newComment = createNewComment(commentDto, commentUser);
        return this.commentPersistence.saveEntityWithReturn(newComment);
    }

    private CommentEntity updateCommentValues(
            CommentEntity verifiedComment,
            CommentRequestDto commentDto
    ) {
        verifiedComment.setMessage(commentDto.message());
        verifiedComment.setModificationDate(LocalDateTime.now());
        logger.warn("Set comment modification date {}", verifiedComment.getModificationDate());
        return verifiedComment;
    }

    private CommentEntity createNewComment(
            CommentRequestDto commentDto,
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
