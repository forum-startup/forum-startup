package org.example.forumstartup.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.post.PostCreateDto;
import org.example.forumstartup.dtos.post.PostResponseDto;
import org.example.forumstartup.dtos.post.PostUpdateDto;
import org.example.forumstartup.dtos.post.PostWithLikeStatusResponseDto;
import org.example.forumstartup.dtos.tags.AddTagsDto;
import org.example.forumstartup.dtos.tags.RemoveTagDto;
import org.example.forumstartup.mappers.PostMapper;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.PostService;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.forumstartup.utils.PageableUtils.parseSort;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
@Tag(name = "Posts", description = "Operations related to forum posts")
public class PostController {

    private final PostService service;
    private final AuthenticationUtils authenticationUtils;
    private final PostMapper mapper;

    /* ------------------------- Public part ------------------------- */

    @GetMapping("/public/posts/count")
    public ResponseEntity<?> getTotalPostCount() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.longToPostTotalCountResponseDto(service.getTotalPostCount()));
    }

    @GetMapping("/public/posts/recent")
    @Operation(summary = "Get most recent posts")
    public ResponseEntity<List<PostResponseDto>> getRecent(
            @RequestParam(defaultValue = "12") int limit
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toSimpleDtoList(service.mostRecent(limit)));
    }

    /* ------------------------- Private part ------------------------- */

    @Operation(summary = "Get all posts")
    @GetMapping("/private/posts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<PostWithLikeStatusResponseDto>> filterPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String searchQuery
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));

        Page<Post> posts = service.filterPosts(searchQuery, pageable);
        User current = authenticationUtils.getAuthenticatedUser();

        return ResponseEntity.ok(
                posts.map(p -> mapper.toAuthenticatedDto(p, current))
        );
    }

    @Operation(
            summary = "Get post by ID (private)",
            description = "Only authenticated users can view posts."
    )
    @ApiResponse(responseCode = "200", description = "Post returned successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @GetMapping("/private/posts/{postId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<PostWithLikeStatusResponseDto> getById(@PathVariable long postId) {
        User actingUser = authenticationUtils.getAuthenticatedUser();
        Post post = service.getById(postId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toAuthenticatedDto(post, actingUser));
    }

    @Operation(
            summary = "Get posts by author (private)",
            description = "Only authenticated users can view posts by a specific user."
    )
    @GetMapping("/private/posts/by-author/{creatorId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<PostWithLikeStatusResponseDto>> getByCreatorId(
            @PathVariable long creatorId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        User actingUser = authenticationUtils.getAuthenticatedUser();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toAuthenticatedDtoList(service.findByCreatorId(creatorId, limit), actingUser));
    }

    @PostMapping("/private/posts")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a post")
    public ResponseEntity<PostWithLikeStatusResponseDto> create(@Valid @RequestBody PostCreateDto dto) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        Post created = service.create(mapper.toPost(dto), currentUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toAuthenticatedDto(created, currentUser));
    }

    @PutMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Edit a post")
    public ResponseEntity<PostWithLikeStatusResponseDto> edit(
            @PathVariable long postId,
            @Valid @RequestBody PostUpdateDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        Post updated = service.edit(postId, mapper.toPostWhenUpdate(dto), currentUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toAuthenticatedDto(updated, currentUser));
    }

    @DeleteMapping("/private/posts/{postId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete a post you own")
    public ResponseEntity<Void> delete(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.delete(postId, currentUser);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/private/posts/{postId}/like")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Like a post")
    public ResponseEntity<Void> like(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.like(postId, currentUser);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PostMapping("/private/posts/{postId}/unlike")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Unlike a post")
    public ResponseEntity<Void> unlike(@PathVariable long postId) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.unlike(postId, currentUser);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /* ------------------------- Admin part ------------------------- */

    @DeleteMapping("/admin/posts/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin deletes any post")
    public ResponseEntity<Void> adminDelete(@PathVariable long postId) {
        User admin = authenticationUtils.getAuthenticatedUser();
        service.adminDelete(postId, admin);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /* ------------------------- Tag operations ------------------------- */

    @GetMapping("/private/posts/by-tag/{tagName}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<PostWithLikeStatusResponseDto>> getPostsByTag(
            @PathVariable String tagName,
            @RequestParam(defaultValue = "10") int limit
    ) {
        User actingUser = authenticationUtils.getAuthenticatedUser();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.toAuthenticatedDtoList(service.findByTag(tagName, limit), actingUser));
    }

    @PostMapping("/private/posts/{postId}/tags")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> addTags(
            @PathVariable long postId,
            @RequestBody @Valid AddTagsDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.addTagsToPost(postId, currentUser, dto.tags());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @DeleteMapping("/private/posts/{postId}/tags")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<Void> removeTag(
            @PathVariable long postId,
            @RequestBody RemoveTagDto dto
    ) {
        User currentUser = authenticationUtils.getAuthenticatedUser();
        service.removeTagFromPost(postId, currentUser, dto.tag());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}