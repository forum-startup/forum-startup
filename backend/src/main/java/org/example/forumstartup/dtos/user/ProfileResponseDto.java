package org.example.forumstartup.dtos.user;

import java.time.LocalDateTime;

public record ProfileResponseDto(
        String firstName,
        String lastName,
        String email,
        String username,
        String profilePhotoUrl,
        LocalDateTime createdAt
) {
}
