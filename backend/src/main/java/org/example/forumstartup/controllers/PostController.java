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
            summary = "Get post by ID ",
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
            summary = "Get posts by author ",
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

    @Operation(summary = "Create a new post",
            description = "Only authenticated users can create posts.")
    @ApiResponse(responseCode = "201", description = "Post created")
    @ApiResponse(responseCode = "400", description = "Invalid request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/private/posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponseDto> create(
            @Valid @RequestBody PostCreateDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        Post created = service.create(currentUser, dto.title(), dto.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(postMapper.toDto(created));
    }

    @Operation(
            summary = "Edit a post",
            description = "Post owner or an admin can edit a post."
    )
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid token")
    @ApiResponse(responseCode = "403", description = "Forbidden – you are neither the owner nor an admin")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @PutMapping("/private/posts/{postId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PostResponseDto> edit(
            @PathVariable long postId,
            @Valid @RequestBody PostUpdateDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        Post updated = service.edit(postId, currentUser, dto.title(), dto.content());
        return ResponseEntity.ok(postMapper.toDto(updated));
    }

    @Operation(
            summary = "Delete a post you own",
            description = "Only the post owner can delete their post. Admins must use the admin delete endpoint."
    )
    @ApiResponse(responseCode = "204", description = "Post deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid token")
    @ApiResponse(responseCode = "403", description = "Forbidden – Only the owner may delete this post")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @DeleteMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.delete(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ===================== PRIVATE LIKE ENDPOINTS =====================

    @Operation(
            summary = "Like a post",
            description = "Authenticated users can like a post. A user cannot like the same post more than once."
    )
    @ApiResponse(responseCode = "204", description = "Post liked successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid token")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @PostMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> like(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.like(postId, currentUser);
        return ResponseEntity.noContent().build();
    }
    @Operation(
            summary = "Unlike a post",
            description = "Authenticated users can remove their like from a post."
    )
    @ApiResponse(responseCode = "204", description = "Like removed")
    @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid token")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @DeleteMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlike(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.unlike(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ===================== ADMIN WRITE ENDPOINTS =====================

    @Operation(
            summary = "Admin deletes any post",
            description = "Admins can delete any post regardless of ownership."
    )
    @ApiResponse(responseCode = "204", description = "Post deleted")
    @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid token")
    @ApiResponse(responseCode = "403", description = "Forbidden – only admins can access this endpoint")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @DeleteMapping("/admin/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable long postId) {
        User admin = authenticationUtils.getAuthenticatedUser();
        service.adminDelete(postId, admin);
        return ResponseEntity.noContent().build();
    }

    // ================= TAG OPERATIONS =================

    @Operation(
            summary = "Get posts by tag",
            description = "Returns a list of posts that have the specified tag. "
    )
    @ApiResponse(responseCode = "200", description = "Posts returned successfully")
    @ApiResponse(responseCode = "404", description = "Tag not found")
    @GetMapping("/public/posts/by-tag/{tagName}")
    public ResponseEntity<List<PostResponseDto>> getPostsByTag(
            @PathVariable String tagName,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                postMapper.toDtoList(service.findByTag(tagName, limit))
        );
    }

    @Operation(
            summary = "Add tags to a post",
            description = "Post owner or an admin can attach one or more tags to the post."
    )
    @ApiResponse(responseCode = "204", description = "Tags added successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid token")
    @ApiResponse(responseCode = "403", description = "Forbidden – only owner or admin may modify tags")
    @ApiResponse(responseCode = "404", description = "Post not found")
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

    @Operation(
            summary = "Remove a tag from a post",
            description = "Post owner or admin can remove a specific tag from the post."
    )
    @ApiResponse(responseCode = "204", description = "Tag removed")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "401", description = "Unauthorized – missing or invalid token")
    @ApiResponse(responseCode = "403", description = "Forbidden – only owner or admin may remove tags")
    @ApiResponse(responseCode = "404", description = "Post or tag not found")
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