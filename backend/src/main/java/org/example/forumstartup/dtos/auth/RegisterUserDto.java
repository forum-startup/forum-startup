package org.example.forumstartup.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import static org.example.forumstartup.utils.StringConstants.*;

public record RegisterUserDto(
        @Size(min = 4, max = 32, message = FIRST_NAME_SIZE_CONSTRAINT_MESSAGE)
        String firstName,

        @Size(min = 4, max = 32, message = LAST_NAME_SIZE_CONSTRAINT_MESSAGE)
        String lastName,

        @Email(message = EMAIL_TYPE_CONSTRAINT_MESSAGE)
        @NotBlank
        String email,

        @NotBlank
        String username,

        @NotBlank
        @Size(min = 6, max = 50, message = PASSWORD_LENGTH_ERROR_MESSAGE)
        String password
) {}
