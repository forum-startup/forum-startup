package org.example.forumstartup.services;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.dtos.comment.UpdateCommentDto;
import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.CommentRepository;
import org.example.forumstartup.repositories.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.forumstartup.utils.StringConstants.*;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public Comment createComment(Long postId, User user, CreateCommentDto dto) {
        ensureNotBlocked(user);

        Post post = getPost(postId);

        Comment parent = null;
        if (dto.parentId() != null) {
            parent = getComment(dto.parentId());
            ensureSamePost(post, parent);
        }

        /*
            TODO
            create a DTO to Comment mapper and use it inside the controller
            so a Comment entity is passed to the service
         */

        Comment comment = buildComment(post, user, parent, dto.content());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment updateComment(Long commentId, User user, UpdateCommentDto dto) {
        ensureNotBlocked(user);

        Comment comment = getComment(commentId);

        ensureAuthorOrAdmin(comment, user);

        comment.setContent(dto.content());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, User user) {
        ensureNotBlocked(user);

        Comment comment = getComment(commentId);

        ensureAuthorOrAdmin(comment, user);

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> listCommentsByPost(Long postId) {
        Post post = getPost(postId);
        return commentRepository.findByPostIdOrderByCreatedAtAsc(post.getId());
    }

    @Override
    @Transactional
    public Comment likeComment(Long commentId, User user) {
        ensureNotBlocked(user);
        Comment comment = getComment(commentId);

        if (comment.getCreator().getId().equals(user.getId())) {
            throw new AuthorizationException(OWN_COMMENT_LIKE_EXCEPTION_MESSAGE);
        }

        if (!comment.getLikedBy().contains(user)) {
            comment.getLikedBy().add(user);
            comment.setLikesCount(comment.getLikesCount() + 1);
        }

        return commentRepository.save(comment);
    }

    /*
        TODO
        Unlike does not check if you have already liked the comment,
        since you cannot unlike a comment you haven't liked

        (frontend will not allow it anyway, but I think a check in the service is necessary)
     */
    @Override
    @Transactional
    public Comment unlikeComment(Long commentId, User user) {
        Comment comment = getComment(commentId);

        if (comment.getLikedBy().remove(user)) {
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
        }

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment adminDeleteComment(Long commentId, User admin) {
        ensureAdmin(admin);

        Comment comment = getComment(commentId);
        commentRepository.delete(comment);
        return comment;
    }


    // private helpers
    private void ensureNotBlocked(User user) {
        if (user.isBlocked()) {
            throw new AuthorizationException(BLOCKED_USER_EXCEPTION_MESSAGE);
        }
    }

    private void ensureAdmin(User user) {
        boolean isAdmin = user.getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals(ERole.ROLE_ADMIN));

        if (!isAdmin)
            throw new AuthorizationException("You are not allowed to perform this action.");
    }

    private void ensureAuthorOrAdmin(Comment comment, User user) {
        boolean isOwner = comment.getCreator().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals(ERole.ROLE_ADMIN));

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException("You are not allowed to modify this comment.");
        }
    }

    private void ensureSamePost(Post post, Comment parent) {
        if (parent != null && !parent.getPost().getId().equals(post.getId())) {
            throw new AuthorizationException(WRONG_POST_REPLY_EXCEPTION_MESSAGE);
        }
    }

    /*
        TODO
        This should be the mapper
     */
    private Comment buildComment(Post post, User creator, Comment parent, String content) {
        Comment c = new Comment();
        c.setPost(post);
        c.setCreator(creator);
        c.setParent(parent);
        c.setContent(content);
        c.setLikesCount(0);
        return c;
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Comment", "id", id.toString()));
    }

    private Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Post", "id", id.toString()));
    }
}
