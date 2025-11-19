package org.example.forumstartup.dtos.post;

import java.time.LocalDateTime;

public record PostResponseDto (
        Long postId,
        Long creatorId,
        String creatorUsername,
        String title,
        String content,
        Integer likesCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}