package org.example.forumstartup.controllers;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.comment.CommentResponseDto;
import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.dtos.comment.UpdateCommentDto;
import org.example.forumstartup.mappers.CommentMapper;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class CommentController {
    private final CommentService commentService;

    /* ================= Public ================= */

    @GetMapping("/public/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponseDto>> listByPost(@PathVariable Long postId) {
        List<Comment> comments = commentService.listCommentsByPost(postId);
        return ResponseEntity.ok(CommentMapper.toDtoList(comments));
    }

    /* ================= Private ================= */

    @PostMapping("/private/posts/{postId}/comments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal User user,
            @RequestBody CreateCommentDto dto
    ) {
        Comment c = commentService.createComment(postId, user, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommentMapper.toDto(c));
    }

    @PutMapping("/private/comments/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> update(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody UpdateCommentDto dto
    ) {
        Comment updated = commentService.updateComment(id, user, dto);
        return ResponseEntity.ok(CommentMapper.toDto(updated));
    }

    @DeleteMapping("/private/comments/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        commentService.deleteComment(id, user);
        return ResponseEntity.noContent().build();
    }

    /* ================= Admin Delete ================= */

    @DeleteMapping("/admin/comments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id, @AuthenticationPrincipal User admin) {
        commentService.adminDeleteComment(id, admin);
        return ResponseEntity.noContent().build();
    }

    /* ================= Likes ================= */

    @PostMapping("/private/comments/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> like(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Comment c = commentService.likeComment(id, user);
        return ResponseEntity.ok(CommentMapper.toDto(c));
    }

    @DeleteMapping("/private/comments/{id}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentResponseDto> unlike(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        Comment c = commentService.unlikeComment(id, user);
        return ResponseEntity.ok(CommentMapper.toDto(c));
    }
}
