package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.comment.CommentResponseDto;
import org.example.forumstartup.models.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentResponseDto toDto(Comment c) {
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

    public static List<CommentResponseDto> toDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toDto).collect(Collectors.toList());
    }
}
