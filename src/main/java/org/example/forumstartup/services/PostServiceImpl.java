package org.example.forumstartup.services;

import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.PostRepository;
import org.example.forumstartup.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", id.toString()));
    }

    @Override
    @Transactional
    public Post create(User creator, String title, String content) {
        User user = userRepository.findById(creator.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("User", "id", creator.getId().toString()));

        if (user.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot create posts");
        }

        Post post = new Post();
        post.setCreator(user);
        post.setTitle(title != null ? title.trim() : null);
        post.setContent(content != null ? content.trim() : null);
        post.setLikesCount(0);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post edit(Long postId, User editor, String titleToUpdate, String contentToUpdate) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        User user = userRepository.findById(editor.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("User", "id", editor.getId().toString()));

        if (user.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot edit posts");
        }

        if (!post.getCreator().getId().equals(user.getId())) {
            throw new AuthorizationException("You are not allowed to edit this post");
        }

        if (titleToUpdate != null && !titleToUpdate.isBlank()) {
            post.setTitle(titleToUpdate.trim());
        }

        if (contentToUpdate != null && !contentToUpdate.isBlank()) {
            post.setContent(contentToUpdate.trim());
        }

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void delete(Long postId, User requester) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        User user = userRepository.findById(requester.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("User", "id", requester.getId().toString()));

        if (user.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot delete posts");
        }

        if (!post.getCreator().getId().equals(user.getId())) {
            throw new AuthorizationException("You are not allowed to delete this post");
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void like(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User", "id", userId.toString()));

        if (user.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot like posts");
        }

        if (post.getCreator().getId().equals(userId)) {
            throw new AuthorizationException("You cannot like your own post");
        }

        if (post.getLikedBy().contains(user)) {
            return;
        }

        post.getLikedBy().add(user);
        Integer current = post.getLikesCount();
        post.setLikesCount((current == null ? 0 : current) + 1);

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void unlike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("User", "id", userId.toString()));

        if (!post.getLikedBy().contains(user)) {
            return;
        }

        post.getLikedBy().remove(user);
        Integer current = post.getLikesCount();
        int newCount = (current == null ? 0 : Math.max(0, current - 1));
        post.setLikesCount(newCount);

        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> mostRecent(int limit) {
        return postRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> topCommented(int limit) {
        return postRepository.findTop10MostCommented();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findByCreatorId(Long id, int limit) {
        List<Post> posts = postRepository.findByCreatorId(
                id,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        return trimToLimit(posts, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> search(String text, int limit) {
        List<Post> posts = postRepository.search(text != null ? text.trim() : "");
        return trimToLimit(posts, limit);
    }

    private List<Post> trimToLimit(List<Post> posts, int limit) {
        if (posts == null || posts.isEmpty()) {
            return posts;
        }
        int max = Math.max(1, Math.min(limit, 50));
        return posts.size() > max ? posts.subList(0, max) : posts;
    }
}