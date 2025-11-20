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

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(ERole.ROLE_ADMIN));
    }

    private void ensureNotBlocked(User user) {
        if (user.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot perform this action.");
        }
    }

    private Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", id.toString()));
    }

    /* ========================= READ METHODS ========================= */

    @Override
    @Transactional(readOnly = true)
    public Post getById(Long id) {
        return getPostOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> mostRecent(int limit) {
        return trimToLimit(postRepository.findTop10ByOrderByCreatedAtDesc(),limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> topCommented(int limit) {
        return trimToLimit(postRepository.findTop10MostCommented(),limit);
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
        List<Post> posts = postRepository.search(text == null ? "" : text.trim());
        return trimToLimit(posts, limit);
    }

    /* ========================= WRITE METHODS ========================= */

    @Override
    @Transactional
    public Post create(User currentUser, String title, String content) {

        ensureNotBlocked(currentUser);
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

        Post post = getPostOrThrow(postId);
        ensureNotBlocked(currentUser);

        boolean isOwner = post.getCreator().getId().equals(currentUser.getId());
        boolean isAdmin = isAdmin(currentUser);

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not allowed to edit this post");
        }

        if (titleToUpdate != null) post.setTitle(titleToUpdate.trim());
        if (contentToUpdate != null) post.setContent(contentToUpdate.trim());

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void delete(Long postId, User currentUser) {

        Post post = getPostOrThrow(postId);

        ensureNotBlocked(currentUser);

        boolean isOwner = post.getCreator().getId().equals(currentUser.getId());
        boolean isAdmin = isAdmin(currentUser);

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not allowed to delete this post");
        }
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void adminDelete(Long postId, User adminUser) {
        ensureNotBlocked(adminUser);
        boolean isAdmin = isAdmin(adminUser);
        if (!isAdmin) {
            throw new AuthorizationException("You are not allowed to delete this post");
        }
        Post post = getPostOrThrow(postId);
        postRepository.delete(post);

    }

    /* ========================= LIKE / UNLIKE ========================= */

    @Override
    @Transactional
    public void like(Long postId, User currentUser) {

        ensureNotBlocked(currentUser);

        Post post = getPostOrThrow(postId);

        if (post.getCreator().getId().equals(currentUser.getId())) {
            throw new AuthorizationException("You cannot like your own post");
        }

        if (!post.getLikedBy().contains(currentUser)) {
            post.getLikedBy().add(currentUser);
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
        }
    }

    @Override
    @Transactional
    public void unlike(Long postId, User currentUser) {

        Post post = getPostOrThrow(postId);

        if (post.getLikedBy().remove(currentUser)) {
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            postRepository.save(post);
        }
    }
}
