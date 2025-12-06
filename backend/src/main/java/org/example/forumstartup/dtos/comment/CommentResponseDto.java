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

        Boolean deleted,
        LocalDateTime deletedAt,
        Long deletedById,
        String deletedByUsername,

        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }
