package org.example.forumstartup.dtos.user;

import org.example.forumstartup.models.Role;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponseDtoForAdmin(
        Long id,
        String firstName,
        String lastName,
        String email,
        String username,
        Set<Role> roles,
        LocalDateTime createdAt,
        boolean isBlocked
) {}
