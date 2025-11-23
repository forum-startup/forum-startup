package org.example.forumstartup.dtos.comment;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        Long postId,
        Long creatorId,
        String creatorUsername,
        String content,
        Long parentId,
        Integer likesCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
