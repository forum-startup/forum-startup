package org.example.forumstartup.dtos.post;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponseDto (
        Long postId,
        Long creatorId,
        String creatorUsername,
        String title,
        String content,
        Integer likesCount,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
}