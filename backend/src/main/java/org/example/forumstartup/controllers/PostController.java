package org.example.forumstartup.controllers;

import jakarta.validation.Valid;
import org.example.forumstartup.dtos.post.PostCreateDto;
import org.example.forumstartup.dtos.post.PostResponseDto;
import org.example.forumstartup.dtos.post.PostUpdateDto;
import org.example.forumstartup.dtos.tags.AddTagsDto;
import org.example.forumstartup.dtos.tags.RemoveTagDto;
import org.example.forumstartup.models.*;
import org.example.forumstartup.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.forumstartup.mappers.PostMapper.toDto;
import static org.example.forumstartup.mappers.PostMapper.toDtoList;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    // ========= PUBLIC READ ENDPOINTS =========

    @GetMapping("/public/posts/{postId}")
    public ResponseEntity<PostResponseDto> getById(@PathVariable long postId) {
        Post post = service.getById(postId);
        return ResponseEntity.ok(toDto(post));
    }

    @GetMapping("/public/posts/by-author/{creatorId}")
    public ResponseEntity<List<PostResponseDto>> getByCreatorId(@PathVariable long creatorId,
                                                                @RequestParam(defaultValue = "10")
                                                                int limit) {
        return ResponseEntity.ok(toDtoList(service.findByCreatorId(creatorId, limit)));
    }

    @GetMapping("/public/posts/recent")
    public ResponseEntity<List<PostResponseDto>> getRecent(@RequestParam(defaultValue = "10")
                                                           int limit) {
        return ResponseEntity.ok(toDtoList(service.mostRecent(limit)));
    }

    @GetMapping("/public/posts/top-commented")
    public ResponseEntity<List<PostResponseDto>> topCommented(@RequestParam(defaultValue = "10")
                                                              int limit) {
        return ResponseEntity.ok(toDtoList(service.topCommented(limit)));
    }

    @GetMapping("/public/posts/search")
    public ResponseEntity<List<PostResponseDto>> search(@RequestParam(name = "word")
                                                        String textToSearch,
                                                        @RequestParam(defaultValue = "10")
                                                        int limit) {
        return ResponseEntity.ok(toDtoList(service.search(textToSearch, limit)));
    }


    // ========= PRIVATE WRITE ENDPOINTS =========

    @PostMapping("/private/posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponseDto> create(@AuthenticationPrincipal User currentUser, @Valid
    @RequestBody PostCreateDto dto) {
        Post newPost = service.create(currentUser, dto.title(), dto.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(newPost));

    }

    @PutMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponseDto> edit(@PathVariable long postId,
                                                @AuthenticationPrincipal User currentUser,
                                                @RequestBody @Valid PostUpdateDto dto
    ) {
        Post updated = service.edit(postId, currentUser, dto.title(), dto.content());
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable long postId,
                                       @AuthenticationPrincipal User currentUser) {
        service.delete(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ========= PRIVATE LIKE ENDPOINTS =========

    @PostMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> like(@PathVariable long postId, @AuthenticationPrincipal User currentUser) {
        service.like(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlike(@PathVariable long postId,
                                       @AuthenticationPrincipal User currentUser) {
        service.unlike(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ========= Admin ENDPOINT =========
    @DeleteMapping("/admin/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable long postId,
                                            @AuthenticationPrincipal User adminUser) {
        service.adminDelete(postId, adminUser);
        return ResponseEntity.noContent().build();
    }
    // ================= TAG OPERATIONS =================

    // Public: browse posts by tag
    @GetMapping("/public/posts/by-tag/{tagName}")
    public ResponseEntity<List<PostResponseDto>> getPostsByTag(
            @PathVariable String tagName,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                toDtoList(service.findByTag(tagName, limit))
        );
    }

    // USER or ADMIN: Add tags to a post
    @PostMapping("/private/posts/{postId}/tags")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> addTags(
            @PathVariable long postId,
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid AddTagsDto dto
    ) {
        service.addTagsToPost(postId, currentUser, dto.tags());
        return ResponseEntity.noContent().build();
    }

    // USER or ADMIN: Remove a tag from a post
    @DeleteMapping("/private/posts/{postId}/tags")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> removeTag(
            @PathVariable long postId,
            @AuthenticationPrincipal User currentUser,
            @RequestBody RemoveTagDto dto
    ) {
        service.removeTagFromPost(postId, currentUser, dto.tag());
        return ResponseEntity.noContent().build();
    }
}