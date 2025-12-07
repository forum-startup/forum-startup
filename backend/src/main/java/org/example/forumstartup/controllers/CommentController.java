package org.example.forumstartup.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.comment.CommentResponseDto;
import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.dtos.comment.UpdateCommentDto;
import org.example.forumstartup.mappers.CommentMapper;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.CommentService;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.example.forumstartup.utils.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@Tag(name = "Comments")
public class CommentController {
    private final CommentService commentService;
    private final AuthenticationUtils authenticationUtils;
    private final CommentMapper mapper;

    /* ------------------------- Private part ------------------------- */

    @Operation(
            summary = "Get comments by post id"
    )
    @GetMapping("/private/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> listByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,asc") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(PageableUtils.parseSort(sort)));
        Page<Comment> comments = commentService.listCommentsByPost(postId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(comments.map(mapper::toDto));
    }

    @Operation(
            summary = "Create comment"
    )
    @PostMapping("/private/posts/{postId}/comments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentDto dto
    ) {
        User user = authenticationUtils.getAuthenticatedUser();
        Comment c = commentService.createComment(postId, user, dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toDto(c));
    }

    @Operation(
            summary = "Update comment"
    )
    @PutMapping("/private/comments/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCommentDto dto
    ) {
        User user = authenticationUtils.getAuthenticatedUser();
        Comment updated = commentService.updateComment(id, user, dto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDto(updated));
    }

    @Operation(
            summary = "Delete comment",
            description = "Soft deletes a comment"
    )
    @DeleteMapping("/private/comments/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> softDelete(@PathVariable Long id) {
        User user = authenticationUtils.getAuthenticatedUser();
        commentService.softDeleteComment(id, user);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Like comment"
    )
    @PostMapping("/private/comments/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> like(@PathVariable Long id) {
        User user = authenticationUtils.getAuthenticatedUser();
        Comment c = commentService.likeComment(id, user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDto(c));
    }

    @Operation(
            summary = "Unlike comment"
    )
    @DeleteMapping("/private/comments/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> unlike(@PathVariable Long id) {
        User user = authenticationUtils.getAuthenticatedUser();
        Comment c = commentService.unlikeComment(id, user);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toDto(c));
    }

    /* ------------------------- Admin part ------------------------- */

    @Operation(
            summary = "Admin delete comment",
            description = "Admin soft deletes any comment"
    )
    @DeleteMapping("/admin/comments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminSoftDelete(@PathVariable Long id) {
        User admin = authenticationUtils.getAuthenticatedUser();
        commentService.softAdminDeleteComment(id, admin);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
