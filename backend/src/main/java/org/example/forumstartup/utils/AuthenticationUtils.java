package org.example.forumstartup.utils;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUtils {
    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName(); // Extracted from JWT automatically

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }
}
