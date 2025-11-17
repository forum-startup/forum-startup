package org.example.forumstartup.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class PostResponseDto {

    private Long postId;
    private Long creatorId;
    private String creatorUsername;
    private String title;
    private String content;
    private Integer likesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}