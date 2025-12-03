package org.example.forumstartup.services;

import org.example.forumstartup.dtos.post.PostResponseDto;
import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.Role;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.PostRepository;
import org.example.forumstartup.repositories.TagRepository;
import org.example.forumstartup.spec.PostSpecs;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.forumstartup.utils.ListUtils.trimToLimit;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TagService tagService;

    public PostServiceImpl(PostRepository postRepository, TagService tagService) {
        this.postRepository = postRepository;
        this.tagService = tagService;
    }
     /*
      it is considered good practice for methods that use other methods
        to be below (parent-child like structure)
     */

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

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    /* ========================= WRITE METHODS ========================= */

    @Override
    @Transactional
    public Post create(Post post, User currentUser) {
        ensureNotBlocked(currentUser);

        post.setCreator(currentUser);
        post.setLikesCount(0);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post edit(Long postId, Post updatePost, User currentUser) {

        Post post = getPostOrThrow(postId);
        ensureUserCanModifyPost(currentUser, post);

        if (updatePost.getTitle() != null) post.setTitle(updatePost.getTitle().trim());
        if (updatePost.getContent() != null) post.setContent(updatePost.getContent().trim());

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

    @Override
    @Transactional(readOnly = true)
    public Page<Post> filterPosts(String username, String text, String tag, Pageable pageable) {

        Specification<Post> spec = Specification.allOf(
                PostSpecs.byUsername(username),
                PostSpecs.byText(text),
                PostSpecs.byTag(tag)
        );

        return postRepository.findAll(spec, pageable);
    }


    /* ========================= HELPER METHODS ========================= */

    /*
     * Spring Security protects the web layer, but it does not know the business rules:
     * who owns posts, who can edit, like, comment, or manage tags,
     * therefore the service layer must still enforce these rules independently.
     */

    @Override
    @Transactional
    public void addTagsToPost(Long postId, User currentUser, List<String> tagNames) {
        Post post = getPostOrThrow(postId);
        ensureUserCanModifyPost(currentUser, post);

        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        for (String rawName : tagNames) {
            Tag tag = tagService.findOrCreate(rawName);
            post.getTags().add(tag);
        }

        postRepository.save(post);
    }

    @Override
    @Transactional
    public void removeTagFromPost(Long postId, User currentUser, String tagName) {
        Post post = getPostOrThrow(postId);
        ensureUserCanModifyPost(currentUser, post);

        Tag tag = tagService.getByName(tagName);

        post.getTags().removeIf(t -> t.getId().equals(tag.getId()));

        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> findByTag(String tagName, int limit) {
        Tag tag = tagService.getByName(tagName); // normalized + validated
        return trimToLimit(postRepository.findPostsByTagName(tag.getName()), limit);
    }

    private boolean isAdmin(User user) {
        for (Role role : user.getRoles()) {
            if (role.getName().equals(ERole.ROLE_ADMIN)) {
                return true;
            }
        }
        return false;
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

    private void ensureUserCanModifyPost(User currentUser, Post post) {

        ensureNotBlocked(currentUser);
        boolean isOwner = post.getCreator().getId().equals(currentUser.getId());
        boolean isAdmin = isAdmin(currentUser);

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not allowed to modify this post.");
        }
    }
}