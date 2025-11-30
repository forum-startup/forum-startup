package org.example.forumstartup.dtos.post;

import java.time.LocalDateTime;
import java.util.List;

public record PostWithLikeStatusResponseDto(
        Long postId,
        Long creatorId,
        String creatorUsername,
        String title,
        String content,
        Integer likesCount,
        boolean likedByCurrentUser,
        List<String> tags,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
