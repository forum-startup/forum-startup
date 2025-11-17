package org.example.forumstartup.models;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
public class PostCreateDto {
    @NotBlank
    @Size(min = 16, max = 64)
    private String title;

    @NotBlank
    @Size(min = 32, max = 8192)
    private String content;
}