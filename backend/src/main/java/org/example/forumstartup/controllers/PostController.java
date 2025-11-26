package org.example.forumstartup.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.post.PostCreateDto;
import org.example.forumstartup.dtos.post.PostResponseDto;
import org.example.forumstartup.dtos.post.PostUpdateDto;
import org.example.forumstartup.dtos.tags.AddTagsDto;
import org.example.forumstartup.dtos.tags.RemoveTagDto;
import org.example.forumstartup.mappers.PostMapper;
import org.example.forumstartup.models.*;
import org.example.forumstartup.services.PostService;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class PostController {
    private final PostService service;
    private final PostMapper mapper;
    private final AuthenticationUtils authenticationUtils;

    // ========= PUBLIC READ ENDPOINTS =========

    @GetMapping("/public/posts/{postId}")
    public ResponseEntity<PostResponseDto> getById(@PathVariable long postId) {
        Post post = service.getById(postId);
        return ResponseEntity.ok(mapper.toDto(post));
    }

    @GetMapping("/public/posts/by-author/{creatorId}")
    public ResponseEntity<List<PostResponseDto>> getByCreatorId(@PathVariable long creatorId,
                                                                @RequestParam(defaultValue = "10")
                                                                int limit) {
        return ResponseEntity.ok(mapper.toDtoList(service.findByCreatorId(creatorId, limit)));
    }

    @GetMapping("/public/posts/recent")
    public ResponseEntity<List<PostResponseDto>> getRecent(@RequestParam(defaultValue = "10")
                                                           int limit) {
        return ResponseEntity.ok(mapper.toDtoList(service.mostRecent(limit)));
    }

    @GetMapping("/public/posts/top-commented")
    public ResponseEntity<List<PostResponseDto>> topCommented(@RequestParam(defaultValue = "10")
                                                              int limit) {
        return ResponseEntity.ok(mapper.toDtoList(service.topCommented(limit)));
    }

    @GetMapping("/public/posts/search")
    public ResponseEntity<List<PostResponseDto>> search(@RequestParam(name = "word")
                                                        String textToSearch,
                                                        @RequestParam(defaultValue = "10")
                                                        int limit) {
        return ResponseEntity.ok(mapper.toDtoList(service.search(textToSearch, limit)));
    }


    // ========= PRIVATE WRITE ENDPOINTS =========

    @PostMapping("/private/posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> create(@Valid @RequestBody PostCreateDto dto) {
        Post post = mapper.toPost(dto);

        service.create(post);
        return ResponseEntity.ok().build();

    }

    @PutMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponseDto> edit(@PathVariable long postId,
                                                @RequestBody @Valid PostUpdateDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        Post updated = service.edit(postId, currentUser, dto.title(), dto.content());
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.delete(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ========= PRIVATE LIKE ENDPOINTS =========

    @PostMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> like(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.like(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlike(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.unlike(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ========= Admin ENDPOINT =========
    @DeleteMapping("/admin/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable long postId) {
        User adminUser = authenticationUtils.getAuthenticatedUser();
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
                mapper.toDtoList(service.findByTag(tagName, limit))
        );
    }

    // USER or ADMIN: Add tags to a post
    @PostMapping("/private/posts/{postId}/tags")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> addTags(
            @PathVariable long postId,
            @RequestBody @Valid AddTagsDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.addTagsToPost(postId, currentUser, dto.tags());
        return ResponseEntity.noContent().build();
    }

    // USER or ADMIN: Remove a tag from a post
    @DeleteMapping("/private/posts/{postId}/tags")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> removeTag(
            @PathVariable long postId,
            @RequestBody RemoveTagDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.removeTagFromPost(postId, currentUser, dto.tag());
        return ResponseEntity.noContent().build();
    }
}