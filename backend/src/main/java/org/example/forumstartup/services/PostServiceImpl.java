package org.example.forumstartup.services;

import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.PostRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.forumstartup.utils.ListUtils.trimToLimit;

/*
    TODO:
    1. Remove user blocked check * blocked users will not be able to login in the first place (part of Spring Security Setup) *
    2. Roles can be checked through Spring Security Context with
 */
@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // ========= READ METHODS =========

    @Override
    @Transactional(readOnly = true)
    public Post getById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", id.toString()));
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

    // ========= WRITE METHODS (changed) =========

    @Override
    @Transactional
    public Post create(User currentUser, String title, String content) {
        // currentUser comes from SecurityContext (@AuthenticationPrincipal)
        if (currentUser.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot create posts");
        }

        Post post = new Post();
        post.setCreator(currentUser);
        post.setTitle(title != null ? title.trim() : null);
        post.setContent(content != null ? content.trim() : null);
        post.setLikesCount(0);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post edit(Long postId, User currentUser,
                     String titleToUpdate, String contentToUpdate) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        if (currentUser.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot edit posts");
        }

        boolean isOwner = post.getCreator().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not allowed to edit this post");
        }
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void delete(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        if (currentUser.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot delete posts");
        }

        boolean isOwner = post.getCreator().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN));

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not allowed to delete this post");
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void like(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        if (currentUser.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot like posts");
        }

        if (post.getCreator().getId().equals(currentUser.getId())) {
            throw new AuthorizationException("You cannot like your own post");
        }

        if (post.getLikedBy().contains(currentUser)) {
            return; // already liked
        }

        post.getLikedBy().add(currentUser);
        Integer current = post.getLikesCount();
        post.setLikesCount((current == null ? 0 : current) + 1);

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void unlike(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", postId.toString()));

        if (!post.getLikedBy().contains(currentUser)) {
            return;
        }
        post.getLikedBy().remove(currentUser);
        Integer current = post.getLikesCount();
        int newCount = (current == null ? 0 : Math.max(0, current - 1));
        post.setLikesCount(newCount);

        postRepository.save(post);
    }
}