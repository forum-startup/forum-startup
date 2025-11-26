package org.example.forumstartup.dtos.tags;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static org.example.forumstartup.utils.TagConstants.TAG_ALLOWED_PATTERN;
import static org.example.forumstartup.utils.TagConstants.TAG_PATTERN_MESSAGE;

public record RemoveTagDto(
        @NotBlank(message = "Tag cannot be blank")
        @Pattern(regexp = TAG_ALLOWED_PATTERN, message = TAG_PATTERN_MESSAGE)
        String tag
) { }
