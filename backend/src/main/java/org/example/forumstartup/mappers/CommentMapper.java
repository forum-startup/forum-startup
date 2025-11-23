package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.comment.CommentResponseDto;
import org.example.forumstartup.models.Comment;
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
}
