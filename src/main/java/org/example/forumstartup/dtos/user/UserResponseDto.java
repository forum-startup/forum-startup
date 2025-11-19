package org.example.forumstartup.dtos.user;

import org.example.forumstartup.models.Role;

import java.util.Set;

public record UserResponseDto(
        String username,
        Set<Role> roles
) {}
