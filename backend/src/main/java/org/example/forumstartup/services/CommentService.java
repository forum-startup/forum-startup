package org.example.forumstartup.services;

import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.dtos.comment.UpdateCommentDto;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.User;

import java.util.List;

public interface CommentService {

    Comment createComment(Long postId, User user, CreateCommentDto dto);

    Comment updateComment(Long commentId, User user, UpdateCommentDto dto);

    void softDeleteComment(Long commentId, User user);

    List<Comment> listCommentsByPost(Long postId);

    Comment likeComment(Long commentId, User user);

    Comment unlikeComment(Long commentId, User user);

    void softAdminDeleteComment(Long commentId, User admin);
}
