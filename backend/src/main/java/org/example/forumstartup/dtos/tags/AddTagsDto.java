package org.example.forumstartup.dtos.tags;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

import static org.example.forumstartup.utils.TagConstants.*;

public record AddTagsDto(
        List<
                @NotBlank(message = "Tag cannot be blank")
                @Size(min = TAG_MIN_LENGTH, max = TAG_MAX_LENGTH,
                        message = "Tag length must be between " + TAG_MIN_LENGTH + " and " + TAG_MAX_LENGTH + " characters.")
                @Pattern(regexp = TAG_ALLOWED_PATTERN, message = TAG_PATTERN_MESSAGE)
                        String
                > tags
) { }
