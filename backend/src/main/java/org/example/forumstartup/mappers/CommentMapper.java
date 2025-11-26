package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.comment.CommentResponseDto;
import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentMapper {

    public CommentResponseDto toDto(Comment c) {
        return new CommentResponseDto(
                c.getId(),
                c.getPost().getId(),
                c.getCreator().getId(),
                c.getCreator().getUsername(),
                c.getContent(),
                c.getParent() != null ? c.getParent().getId() : null,
                c.getLikesCount(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    public List<CommentResponseDto> toDtoList(List<Comment> comments) {
        return comments.stream()
                .map(this::toDto)
                .toList();
    }

    public Comment createFromDto(Post post, User creator, Comment parent, CreateCommentDto dto) {
        Comment c = new Comment();
        c.setPost(post);
        c.setCreator(creator);
        c.setParent(parent);
        c.setContent(dto.content());
        c.setLikesCount(0);
        return c;
    }
}
