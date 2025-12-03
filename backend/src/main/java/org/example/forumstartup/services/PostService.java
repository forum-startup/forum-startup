package org.example.forumstartup.services;

import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {

    Post create(Post post, User currentUser);

    Post edit(Long postId, Post post, User currentUser);

    void delete(Long postId, User currentUser);

    void adminDelete(Long postId, User adminUser);

    void like(Long postId, User currentUser);

    void unlike(Long postId, User currentUser);

    Post getById(Long id);

    List<Post> mostRecent(int limit);

    List<Post> topCommented(int limit);

    List<Post> findByCreatorId(Long id, int limit);

    List<Post> search(String text, int limit);

    List<Post> getAll();

    void addTagsToPost(Long postId, User currentUser, List<String> tagNames);

    void removeTagFromPost(Long postId, User currentUser, String tagName);

    List<Post> findByTag(String tagName, int limit);

    Page<Post> filterPosts(String username, String text, String tag, Pageable pageable);
}
