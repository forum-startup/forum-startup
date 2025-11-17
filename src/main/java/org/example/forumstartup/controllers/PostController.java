package org.example.forumstartup.controllers;

import jakarta.validation.Valid;
import org.example.forumstartup.models.*;
import org.example.forumstartup.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.forumstartup.helpers.PostMapper.toDto;
import static org.example.forumstartup.helpers.PostMapper.toDtoList;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    // ========= PUBLIC READ ENDPOINTS =========

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getById(@PathVariable long postId) {
        Post post = service.getById(postId);
        return ResponseEntity.ok(toDto(post));
    }

    @GetMapping("/by-author/{creatorId}")
    public ResponseEntity<List<PostResponseDto>> getByCreatorId(@PathVariable long creatorId,
                                                                @RequestParam(defaultValue = "10") int limit) {
        List<Post> posts = service.findByCreatorId(creatorId, limit);
        List<PostResponseDto> result = toDtoList(posts);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PostResponseDto>> getRecent(@RequestParam(defaultValue = "10") int limit) {
        List<Post> posts = service.mostRecent(limit);
        List<PostResponseDto> result = toDtoList(posts);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/top-commented")
    public ResponseEntity<List<PostResponseDto>> topCommented(@RequestParam(defaultValue = "10") int limit) {
        List<Post> posts = service.topCommented(limit);
        List<PostResponseDto> result = toDtoList(posts);
        return ResponseEntity.ok(result);

    }

    @GetMapping("/search")
    public ResponseEntity<List<PostResponseDto>> search(@RequestParam(name = "word") String textToSearch,
                                                        @RequestParam(defaultValue = "10") int limit) {
        List<Post> posts = service.search(textToSearch, limit);
        List<PostResponseDto> result = toDtoList(posts);
        return ResponseEntity.ok(result);
    }
    // ========= PRIVATE WRITE ENDPOINTS (JWT/BASIC AUTH REQUIRED) =========

    @PostMapping
    public ResponseEntity<PostResponseDto> create(@AuthenticationPrincipal User currentUser, @Valid
    @RequestBody PostCreateDto dto) {
        Post newPost = service.create(currentUser, dto.getTitle(), dto.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(newPost));

    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> edit(@PathVariable long postId,
                                                @AuthenticationPrincipal User currentUser,
                                                @RequestBody @Valid PostUpdateDto dto
    ) {
        Post updated = service.edit(postId, currentUser, dto.getTitle(), dto.getContent());
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable long postId,
                                       @AuthenticationPrincipal User currentUser) {
        service.delete(postId, currentUser);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> like(@PathVariable long postId, @AuthenticationPrincipal User currentUser) {
        service.like(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlike(@PathVariable long postId,
                                       @AuthenticationPrincipal User currentUser) {
        service.unlike(postId, currentUser);
        return ResponseEntity.noContent().build();
    }
}