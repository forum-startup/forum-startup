package org.example.forumstartup.services;

import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;

import java.util.List;

public interface PostService {
    Post create(User creator, String title, String content);

    Post getById(Long id);

    Post edit(Long postId, User creator, String updatedTitle, String updatedContent);

    void delete(Long postId, User creator);

    void like(Long postId, Long creatorId);

    void unlike(Long postId, Long creatorId);

    List<Post> mostRecent(int limit);

    List<Post> topCommented(int limit);

    List<Post> findByCreatorId(Long id, int limit);

    List<Post> search(String text, int limit);
}
