package bg.exploreBG.web;

import bg.exploreBG.model.dto.ApiResponse;
import bg.exploreBG.model.dto.comment.CommentDto;
import bg.exploreBG.model.dto.comment.validate.CommentUpdateDto;
import bg.exploreBG.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDto>> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateDto commentUpdateDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CommentDto commentDto =
                this.commentService
                        .updateComment(id, commentUpdateDto, userDetails);

        ApiResponse<CommentDto> response = new ApiResponse<>(commentDto);

        return ResponseEntity.ok(response);
    }
}
