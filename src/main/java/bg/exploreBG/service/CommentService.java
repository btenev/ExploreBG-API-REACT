package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.comment.validate.CommentUpdateDto;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.mapper.CommentMapper;
import bg.exploreBG.querybuilder.CommentQueryBuilder;
import bg.exploreBG.repository.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentMapper commentMapper;
    private final GenericPersistenceService<CommentEntity> commentPersistence;
    private final CommentQueryBuilder commentQueryBuilder;

    public CommentService(
            CommentMapper commentMapper,
            GenericPersistenceService<CommentEntity> commentPersistence,
            CommentQueryBuilder commentQueryBuilder
    ) {
        this.commentMapper = commentMapper;
        this.commentPersistence = commentPersistence;
        this.commentQueryBuilder = commentQueryBuilder;
    }

    public CommentDto updateComment(
            Long commentId,
            CommentUpdateDto commentDto,
            UserDetails userDetails
    ) {
        CommentEntity verifiedComment = this.commentQueryBuilder
                .getCommentEntityByIdIfOwner(commentId, userDetails.getUsername());
        CommentEntity updated = updateCommentValues(verifiedComment, commentDto);

        CommentEntity saved = this.commentPersistence.saveEntityWithReturn(updated);

        return this.commentMapper.commentEntityToCommentDto(saved);
    }

    public CommentEntity saveComment(CommentCreateDto commentDto, UserEntity commentUser) {
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
