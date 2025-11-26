package org.example.forumstartup.dtos.tags;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import static org.example.forumstartup.utils.TagConstants.TAG_ALLOWED_PATTERN;
import static org.example.forumstartup.utils.TagConstants.TAG_PATTERN_MESSAGE;

public record AddTagsDto(
        List<
                @NotBlank(message = "Tag cannot be blank")
                @Pattern(regexp = TAG_ALLOWED_PATTERN, message = TAG_PATTERN_MESSAGE)
                        String
                > tags
) { }
