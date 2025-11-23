package org.example.forumstartup.dtos.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCommentDto(
        @NotBlank
        @Size(min = 1, max = 1000)
        String content
) { }
