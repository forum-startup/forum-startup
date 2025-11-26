package org.example.forumstartup.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.forumstartup.dtos.post.PostCreateDto;
import org.example.forumstartup.dtos.post.PostResponseDto;
import org.example.forumstartup.dtos.post.PostUpdateDto;
import org.example.forumstartup.dtos.tags.AddTagsDto;
import org.example.forumstartup.dtos.tags.RemoveTagDto;
import org.example.forumstartup.mappers.PostMapper;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.PostService;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Posts", description = "Operations related to forum posts")
public class PostController {

    private final PostService service;
    private final AuthenticationUtils authenticationUtils;
    private final PostMapper postMapper;

    public PostController(PostService service,
                          AuthenticationUtils authenticationUtils,
                          PostMapper postMapper) {
        this.service = service;
        this.authenticationUtils = authenticationUtils;
        this.postMapper = postMapper;
    }

    // ===================== PUBLIC READ ENDPOINTS =====================

    @GetMapping("/public/posts/recent")
    @Operation(summary = "Get most recent posts")
    public ResponseEntity<List<PostResponseDto>> getRecent(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(postMapper.toDtoList(service.mostRecent(limit)));
    }

    @GetMapping("/public/posts/top-commented")
    @Operation(summary = "Get most commented posts")
    public ResponseEntity<List<PostResponseDto>> topCommented(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(postMapper.toDtoList(service.topCommented(limit)));
    }

    @GetMapping("/public/posts/search")
    @Operation(summary = "Search posts")
    public ResponseEntity<List<PostResponseDto>> search(
            @RequestParam(name = "word") String query,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(postMapper.toDtoList(service.search(query, limit)));
    }

// ===================== PRIVATE READ ENDPOINTS =====================

    @Operation(
            summary = "Get post by ID (private)",
            description = "Only authenticated users can view posts."
    )
    @ApiResponse(responseCode = "200", description = "Post returned successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @GetMapping("/private/posts/{postId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PostResponseDto> getById(@PathVariable long postId) {
        authenticationUtils.getAuthenticatedUser();
        Post post = service.getById(postId);
        return ResponseEntity.ok(postMapper.toDto(post));
    }

    @Operation(
            summary = "Get posts by author (private)",
            description = "Only authenticated users can view posts by a specific user."
    )
    @GetMapping("/private/posts/by-author/{creatorId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<PostResponseDto>> getByCreatorId(
            @PathVariable long creatorId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        authenticationUtils.getAuthenticatedUser(); // ensures logged-in
        return ResponseEntity.ok(
                postMapper.toDtoList(service.findByCreatorId(creatorId, limit))
        );
    }

    // ===================== PRIVATE WRITE ENDPOINTS =====================

    @PostMapping("/private/posts")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a post")
    public ResponseEntity<PostResponseDto> create(
            @Valid @RequestBody PostCreateDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        Post created = service.create(currentUser, dto.title(), dto.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDto(created));
    }

    @PutMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Edit a post")
    public ResponseEntity<PostResponseDto> edit(
            @PathVariable long postId,
            @Valid @RequestBody PostUpdateDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        Post updated = service.edit(postId, currentUser, dto.title(), dto.content());
        return ResponseEntity.ok(postMapper.toDto(updated));
    }

    @DeleteMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a post you own")
    public ResponseEntity<Void> delete(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.delete(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ===================== PRIVATE LIKE ENDPOINTS =====================

    @PostMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Like a post")
    public ResponseEntity<Void> like(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.like(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Unlike a post")
    public ResponseEntity<Void> unlike(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.unlike(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ===================== ADMIN WRITE ENDPOINTS =====================

    @DeleteMapping("/admin/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin deletes any post")
    public ResponseEntity<Void> adminDelete(@PathVariable long postId) {
        User admin = authenticationUtils.getAuthenticatedUser();
        service.adminDelete(postId, admin);
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
                postMapper.toDtoList(service.findByTag(tagName, limit))
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