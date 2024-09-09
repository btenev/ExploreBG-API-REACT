package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentCreateDto;
import bg.exploreBG.model.dto.comment.validate.CommentUpdateDto;
import bg.exploreBG.model.entity.CommentEntity;
import bg.exploreBG.model.entity.UserEntity;
import bg.exploreBG.model.mapper.CommentMapper;
import bg.exploreBG.repository.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    public CommentService(
            CommentRepository commentRepository,
            CommentMapper commentMapper
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
    }

    public CommentDto updateComment(
            Long commentId,
            CommentUpdateDto commentDto,
            UserDetails userDetails
    ) {
        CommentEntity verifiedComment = getCommentEntityByIdIfOwner(commentId, userDetails.getUsername());
        CommentEntity updated = updateCommentValues(verifiedComment, commentDto);

        CommentEntity saved = this.commentRepository.save(updated);

        return this.commentMapper.commentEntityToCommentDto(saved);
    }

    public CommentEntity saveComment(CommentCreateDto commentDto, UserEntity commentUser) {
        CommentEntity newComment = createNewComment(commentDto, commentUser);
        return this.commentRepository.save(newComment);
    }

    public void deleteCommentById(Long commentId) {
        this.commentRepository.deleteById(commentId);
        ;
    }

    private CommentEntity updateCommentValues(
            CommentEntity verifiedComment,
            CommentUpdateDto commentDto
    ) {
        verifiedComment.setMessage(commentDto.message());
        verifiedComment.setModificationDate(LocalDateTime.now());
        return verifiedComment;
    }

    private CommentEntity createNewComment(
            CommentCreateDto commentDto,
            UserEntity verifiedUser
    ) {
        CommentEntity newComment = new CommentEntity();
        newComment.setMessage(commentDto.message());
        newComment.setCreationDate(LocalDateTime.now());
        newComment.setOwner(verifiedUser);

        return newComment;
    }

    public void validateCommentOwnership(Long commentId, String email) {
        boolean isOwner = this.commentRepository.isUserOwnerOfComment(commentId, email);

        if (!isOwner) {
            throw new AppException("Comment not found or user is not the owner!", HttpStatus.FORBIDDEN);
        }
    }

    private CommentEntity getCommentEntityByIdIfOwner(Long commentId, String email) {
        return this.commentRepository
                .findByIdAndOwnerEmail(commentId, email)
                .orElseThrow(() -> new AppException("Comment not found or is not owned by the specified user!",
                        HttpStatus.BAD_REQUEST));
    }
}
