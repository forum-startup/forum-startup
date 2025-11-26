package org.example.forumstartup.services;

import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;

import java.util.List;

public interface PostService {
    void create(Post post);

    Post edit(Long postId, User currentUser, String titleToUpdate, String contentToUpdate);

    void delete(Long postId, User currentUser);

    void adminDelete(Long postId, User adminUser);

    void like(Long postId, User currentUser);

    void unlike(Long postId, User currentUser);

    Post getById(Long id);

    List<Post> mostRecent(int limit);

    List<Post> topCommented(int limit);

    List<Post> findByCreatorId(Long id, int limit);

    List<Post> search(String text, int limit);

}
