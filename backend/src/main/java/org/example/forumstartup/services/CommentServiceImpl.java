package org.example.forumstartup.services;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.dtos.comment.UpdateCommentDto;
import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.mappers.CommentMapper;
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
    private final CommentMapper commentMapper;

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

        Comment comment = commentMapper.createFromDto(post, user, parent, dto);
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

    @Override
    @Transactional
    public Comment unlikeComment(Long commentId, User user) {
        Comment comment = getComment(commentId);

        boolean removed = comment.getLikedBy().remove(user);
        if (!removed) {
            return comment;
        }

        comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
        return comment;
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
            throw new AuthorizationException(UNAUTHORIZED_ACTION_EXCEPTION_MESSAGE);
    }

    private void ensureAuthorOrAdmin(Comment comment, User user) {
        boolean isOwner = comment.getCreator().getId().equals(user.getId());
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.getName().equals(ERole.ROLE_ADMIN));

        if (!isOwner && !isAdmin) {
            throw new AuthorizationException(COMMENT_MODIFICATION_EXCEPTION_MESSAGE);
        }
    }

    private void ensureSamePost(Post post, Comment parent) {
        if (parent != null && !parent.getPost().getId().equals(post.getId())) {
            throw new AuthorizationException(WRONG_POST_REPLY_EXCEPTION_MESSAGE);
        }
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
