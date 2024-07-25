package bg.exploreBG.service;

import bg.exploreBG.exception.AppException;
import bg.exploreBG.model.dto.comment.single.CommentMessageDto;
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
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;


    public CommentService(
            CommentRepository commentRepository,
            UserService userService,
            CommentMapper commentMapper
    ) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.commentMapper = commentMapper;
    }

    public CommentMessageDto updateComment(
            Long id,
            CommentUpdateDto commentDto,
            UserDetails userDetails
    ) {
        CommentEntity verifiedComment = verifiedComment(id, userDetails);
        CommentEntity updated = updateCommentValues(verifiedComment, commentDto);
        CommentEntity saved = this.commentRepository.save(updated);
        return new CommentMessageDto(saved.getMessage());
    }

    private CommentEntity updateCommentValues(
            CommentEntity verifiedComment,
            CommentUpdateDto commentDto
    ) {
        verifiedComment.setMessage(commentDto.message());
        verifiedComment.setModificationDate(LocalDateTime.now());
        return verifiedComment;
    }

    public CommentEntity verifiedComment(
            Long commentId,
            UserDetails userDetails
    ) {
        CommentEntity existingComment = commentExist(commentId);
        UserEntity commentOwner = existingComment.getOwner();

        this.userService.verifiedUser(commentOwner, userDetails);

        return existingComment;
    }

    private CommentEntity commentExist(Long id) {
        Optional<CommentEntity> byId = this.commentRepository.findById(id);

        if (byId.isEmpty()) {
            throw new AppException("Commend not found!", HttpStatus.NOT_FOUND);
        }

        return byId.get();
    }

    public CommentEntity createNewComment(
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
