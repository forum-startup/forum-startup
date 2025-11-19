package org.example.forumstartup.dtos.post;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record PostUpdateDto(
        @NotBlank
        @Size(min = 16, max = 64)
        String title,

        @NotBlank
        @Size(min = 32, max = 8192)
        String content
) {
}
