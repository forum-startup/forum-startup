package org.example.forumstartup.controllers;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.services.TagService;
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
public class TagController {

    private final TagService tagService;

    /* ------------------------- Public part ------------------------- */

    /**
     * List all tags alphabetically
     */
    @GetMapping("/public/tags")
    public ResponseEntity<List<String>> listAllTags() {
        List<String> tags = tagService
                .getAll()
                .stream()
                .map(Tag::getName)
                .sorted()
                .toList();

        return ResponseEntity.ok(tags);
    }

    @GetMapping("/public/tags/{tagName}")
    public ResponseEntity<String> getTagByName(@PathVariable String tagName) {
        Tag tag = tagService.getByName(tagName);
        return ResponseEntity.ok(tag.getName());
    }
}
