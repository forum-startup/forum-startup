package org.example.forumstartup.controllers;

import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getById(@PathVariable long postId) {
        return ResponseEntity.ok(service.getById(postId));
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Void> like(@PathVariable long postId, @RequestParam long userId) {
        service.like(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Void> unlike(@PathVariable long postId,
                                       @RequestParam long userId) {
        service.unlike(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Post> create(@RequestParam long creatorId,
                                       @RequestParam String title,
                                       @RequestParam String content) {
        User creator = new User();
        creator.setId(creatorId);
        Post newPost = service.create(creator, title, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPost);

    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> edit(@PathVariable long postId,
                                     @RequestParam long creatorId,
                                     @RequestParam(required = false) String titleToUpdate,
                                     @RequestParam(required = false) String contentToUpdate) {
        User author = new User();
        author.setId(creatorId);

        Post updated = service.edit(postId, author, titleToUpdate, contentToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable long postId,
                                       @RequestParam long creatorId) {

        User user = new User();
        user.setId(creatorId);
        service.delete(postId, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-author/{creatorId}")
    public ResponseEntity<List<Post>> getByCreatorId(@PathVariable long creatorId,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.findByCreatorId(creatorId, limit));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Post>> getRecent(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.mostRecent(limit));
    }

    @GetMapping("/top-commented")
    public ResponseEntity<List<Post>> topCommented(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.topCommented(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> search(@RequestParam(name = "word") String textToSearch,
                                             @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.search(textToSearch, limit));
    }
}
