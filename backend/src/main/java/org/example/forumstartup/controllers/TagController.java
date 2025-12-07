package org.example.forumstartup.controllers;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.services.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags")
public class TagController {

    private final TagService tagService;

    /* ------------------------- Public part ------------------------- */

    @Operation(
            summary = "Get all tags alphabetically"
    )
    @GetMapping("/public/tags")
    public ResponseEntity<List<String>> listAllTags() {
        List<String> tags = tagService
                .getAll()
                .stream()
                .map(Tag::getName)
                .sorted()
                .toList();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tags);
    }

    @Operation(
            summary = "Get a tag by its name"
    )
    @GetMapping("/public/tags/{tagName}")
    public ResponseEntity<String> getTagByName(@PathVariable String tagName) {
        Tag tag = tagService.getByName(tagName);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(tag.getName());
    }
}
