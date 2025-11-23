package org.example.forumstartup.dtos.tags;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AddTagsDto(List<@NotBlank(message = "Tag cannot be blank") String> tags) {
}
