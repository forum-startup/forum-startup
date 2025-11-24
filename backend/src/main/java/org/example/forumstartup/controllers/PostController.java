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
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.forumstartup.mappers.PostMapper.toDto;
import static org.example.forumstartup.mappers.PostMapper.toDtoList;

@Tag(
        name = "Posts",
        description = "Public, private, and admin operations related to forum posts"
)
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    // ===================== PUBLIC ENDPOINTS =====================

    @Operation(
            summary = "Get post by ID",
            description = "Returns a single post including its content, creator info, comments, likes, and tags."
    )
    @ApiResponse(responseCode = "200", description = "Post returned successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @GetMapping("/public/posts/{postId}")
    public ResponseEntity<PostResponseDto> getById(@PathVariable long postId) {
        Post post = service.getById(postId);
        return ResponseEntity.ok(toDto(post));
    }

    @Operation(
            summary = "Get posts by author",
            description = "Returns posts created by a specific user, ordered by creation date."
    )
    @GetMapping("/public/posts/by-author/{creatorId}")
    public ResponseEntity<List<PostResponseDto>> getByCreatorId(
            @PathVariable long creatorId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(toDtoList(service.findByCreatorId(creatorId, limit)));
    }

    @Operation(
            summary = "Get recent posts",
            description = "Returns the most recently created posts."
    )
    @GetMapping("/public/posts/recent")
    public ResponseEntity<List<PostResponseDto>> getRecent(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(toDtoList(service.mostRecent(limit)));
    }

    @Operation(
            summary = "Get top commented posts",
            description = "Returns posts ordered by number of comments (descending)."
    )
    @GetMapping("/public/posts/top-commented")
    public ResponseEntity<List<PostResponseDto>> topCommented(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(toDtoList(service.topCommented(limit)));
    }

    @Operation(
            summary = "Search posts",
            description = "Search posts by keyword in title or content."
    )
    @GetMapping("/public/posts/search")
    public ResponseEntity<List<PostResponseDto>> search(
            @RequestParam(name = "word") String textToSearch,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(toDtoList(service.search(textToSearch, limit)));
    }


    // ===================== PRIVATE USER ENDPOINTS =====================

    @Operation(
            summary = "Create a new post",
            description = "Authenticated users can create new posts with a title and content."
    )
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @PostMapping("/private/posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponseDto> create(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody PostCreateDto dto
    ) {
        Post newPost = service.create(currentUser, dto.title(), dto.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(newPost));
    }

    @Operation(summary = "Edit an existing post")
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "403", description = "User is not owner or admin")
    @PutMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponseDto> edit(
            @PathVariable long postId,
            @AuthenticationPrincipal User currentUser,
            @RequestBody @Valid PostUpdateDto dto
    ) {
        Post updated = service.edit(postId, currentUser, dto.title(), dto.content());
        return ResponseEntity.ok(toDto(updated));
    }

    @Operation(summary = "Delete a post you own")
    @ApiResponse(responseCode = "204", description = "Post deleted")
    @ApiResponse(responseCode = "403", description = "User is not owner or admin")
    @DeleteMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> delete(
            @PathVariable long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        service.delete(postId, currentUser);
        return ResponseEntity.noContent().build();
    }


    // ===================== LIKE & UNLIKE =====================

    @Operation(summary = "Like a post")
    @PostMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> like(
            @PathVariable long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        service.like(postId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove like from a post")
    @DeleteMapping("/private/posts/{postId}/likes")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> unlike(
            @PathVariable long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        service.unlike(postId, currentUser);
        return ResponseEntity.noContent().build();
    }


    // ===================== ADMIN ENDPOINT =====================

    @Operation(
            summary = "Admin delete ANY post",
            description = "Admins can delete posts from any user."
    )
    @ApiResponse(responseCode = "204", description = "Post deleted")
    @DeleteMapping("/admin/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(
            @PathVariable long postId,
            @AuthenticationPrincipal User adminUser
    ) {
        service.adminDelete(postId, adminUser);
        return ResponseEntity.noContent().build();
    }


    // ===================== TAG OPERATIONS =====================

    @Operation(summary = "Get posts by tag")
    @GetMapping("/public/posts/by-tag/{tagName}")
    public ResponseEntity<List<PostResponseDto>> getPostsByTag(
            @PathVariable String tagName,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(toDtoList(service.findByTag(tagName, limit)));
    }

    @Operation(summary = "Add tags to a post")
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

    @Operation(summary = "Remove a tag from a post")
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