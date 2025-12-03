package org.example.forumstartup.dtos.comment;

import java.time.LocalDateTime;

/*
    TODO
    I will be changing the DTO response info depending on what's needed on the frontend.
    Will keep this note here for now
 */
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
