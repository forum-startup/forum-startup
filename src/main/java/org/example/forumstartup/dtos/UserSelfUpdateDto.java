package org.example.forumstartup.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import static org.example.forumstartup.utils.StringConstants.*;

public record UserSelfUpdateDto(
        @Size(min = 4, max = 32, message = FIRST_NAME_SIZE_CONSTRAINT_MESSAGE)
        String firstName,

        @Size(min = 4, max = 32, message = LAST_NAME_SIZE_CONSTRAINT_MESSAGE)
        String lastName,

        @Email(message = EMAIL_TYPE_CONSTRAINT_MESSAGE)
        String email,

        @URL
        String profilePhotoUrl,

        @Size(min = 6, max = 50)
        String password
) {}
