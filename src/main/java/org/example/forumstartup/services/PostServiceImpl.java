package org.example.forumstartup.services;

import org.example.forumstartup.enums.Role;
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
import static org.example.forumstartup.utils.StringConstants.*;

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
    public Post getById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post", "id", String.valueOf(postId)));
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
    public List<Post> findByAuthorId(Long id, int limit) {
        List<Post> posts = postRepository.findByCreatorId(id, Sort.by(Sort.Direction.DESC, "createdAt"));
        return trimToLimit(posts, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> search(String text, int limit) {
        List<Post> posts = postRepository.search(text != null ? text.trim() : "");
        return trimToLimit(posts, limit);
    }


    @Override
    @Transactional
    public Post create(User actingUser, String title, String content) {
        User actor = userRepository.findById(actingUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(actingUser.getId())));

        if (actor.isBlocked()) {
            throw new AuthorizationException("User is blocked and cannot create posts.");
        }

        Post post = new Post();
        post.setCreator(actor);
        post.setTitle(title != null ? title.trim() : null);
        post.setContent(content != null ? content.trim() : null);
        post.setLikesCount(0);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post edit(Long postId, User actingUser, String titleToUpdate, String contentToUpdate) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post", "id", String.valueOf(postId)));

        User actor = userRepository.findById(actingUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(actingUser.getId())));

        if (actor.isBlocked()) {
            throw new AuthorizationException("User is blocked and cannot edit posts.");
        }

        boolean admin = isAdmin(actor);
        if (!admin && !actor.getId().equals(post.getCreator().getId())) {
            throw new AuthorizationException(UNAUTHORIZED_ACTION_EXCEPTION_MESSAGE);
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
    public void delete(Long postId, User actingUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post", "id", String.valueOf(postId)));

        User actor = userRepository.findById(actingUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(actingUser.getId())));

        if (actor.isBlocked()) {
            throw new AuthorizationException("User is blocked and cannot delete posts.");
        }

        boolean admin = isAdmin(actor);
        if (!admin && !actor.getId().equals(post.getCreator().getId())) {
            throw new AuthorizationException(UNAUTHORIZED_ACTION_EXCEPTION_MESSAGE);
        }

        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void like(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post", "id", String.valueOf(postId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(userId)));

        if (user.isBlocked()) {
            throw new AuthorizationException("User is blocked and cannot like posts.");
        }
        if (post.getCreator().getId().equals(userId)) {
            throw new AuthorizationException("You cannot like your own post.");
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
                .orElseThrow(() -> new EntityNotFoundException("Post", "id", String.valueOf(postId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", String.valueOf(userId)));

        if (!post.getLikedBy().contains(user)) {
            return;
        }

        post.getLikedBy().remove(user);
        Integer current = post.getLikesCount();
        int newCount = (current == null ? 0 : Math.max(0, current - 1));
        post.setLikesCount(newCount);

        postRepository.save(post);
    }
    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private List<Post> trimToLimit(List<Post> posts, int limit) {
        if (posts == null || posts.isEmpty()) return posts;
        int max = Math.max(1, Math.min(limit, 50));
        return posts.size() > max ? posts.subList(0, max) : posts;
    }
}