package org.example.forumstartup.dtos.jwt;

import org.example.forumstartup.models.Role;

import java.util.Set;

public record JwtResponseDto(
        String username,
        Set<Role> roles
) {}
