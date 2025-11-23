package org.example.forumstartup.services;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.PostRepository;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.example.forumstartup.repositories.TagRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.forumstartup.utils.ListUtils.trimToLimit;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AuthenticationUtils authenticationUtils;
    private final TagRepository tagRepository;

    /* Checks if a user has a role as an Admi */
    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(ERole.ROLE_ADMIN));
    }

    /*
     * Service-layer validation for blocked users.
     * Needed even with security config because:
     * - JWT is stateless → blocked users may still have a valid token.
     * - Business rules should always be enforced in the service layer.
     */
    private void ensureNotBlocked(User user) {
        if (user.isBlocked()) {
            throw new AuthorizationException("Blocked users cannot perform this action.");
        }
    }

    /* load a post or throw clean exception. */
    private Post getPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", id.toString()));
    }
    /* Does service authZ to ensure proper rights */

    private void ensureUserCanModifyPost(User currentUser, Post post) {

        ensureNotBlocked(currentUser);
        boolean isOwner = post.getCreator().getId().equals(currentUser.getId());
        boolean isAdmin = isAdmin(currentUser);

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not allowed to modify this post.");
        }
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
        return trimToLimit(postRepository.findTop10ByOrderByCreatedAtDesc(), limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> topCommented(int limit) {
        return trimToLimit(postRepository.findTop10MostCommented(), limit);
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
    public void create(Post post) {
        User actingUser = authenticationUtils.getAuthenticatedUser();

        post.setCreator(actingUser);
        post.setLikesCount(0);

        postRepository.save(post);
    }

    @Override
    @Transactional
    public Post edit(Long postId, User currentUser,
                     String titleToUpdate, String contentToUpdate) {

        Post post = getPostOrThrow(postId);
        ensureUserCanModifyPost(currentUser, post);

        if (titleToUpdate != null) post.setTitle(titleToUpdate.trim());
        if (contentToUpdate != null) post.setContent(contentToUpdate.trim());

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void delete(Long postId, User currentUser) {

        Post post = getPostOrThrow(postId);
        ensureUserCanModifyPost(currentUser, post);
        postRepository.delete(post);
    }
    /*
     * Admin-only delete. Controller enforces @PreAuthorize,
     * but service still checks for safety.
     */
    @Override
    @Transactional
    public void adminDelete(Long postId, User adminUser) {
        ensureNotBlocked(adminUser);
        if (!isAdmin(adminUser)) {
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

    /* ========================= TAG WRITE METHODS ========================= */
    @Override
    @Transactional
    public void addTagsToPost(Long postId, User currentUser, List<String> tagNames) {
        Post post = getPostOrThrow(postId);
        ensureUserCanModifyPost(currentUser, post);

        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        for (String rawName : tagNames) {
            if (rawName == null || rawName.isBlank()) continue;

            String normalized = rawName.trim().toLowerCase();

            Tag tag = tagRepository.findByName(normalized)
                    .orElseGet(() -> {
                        Tag t = new Tag();
                        t.setName(normalized);
                        return tagRepository.save(t);
                    });

            post.getTags().add(tag);
        }

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void removeTagFromPost(Long postId, User currentUser, String tagName) {
        Post post = getPostOrThrow(postId);
        ensureUserCanModifyPost(currentUser, post);

        if (tagName == null || tagName.isBlank()) return;

        String normalized = tagName.trim().toLowerCase();
        post.getTags().removeIf(tag -> tag.getName().equals(normalized));

        postRepository.save(post);
    }

    /* ========================= TAG READ METHOD ========================= */

    @Override
    @Transactional(readOnly = true)
    public List<Post> findByTag(String tagName, int limit) {
        String normalized = tagName == null ? "" : tagName.trim().toLowerCase();
        return trimToLimit(postRepository.findPostsByTagName(normalized), limit);
    }
}
