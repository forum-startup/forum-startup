package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.comment.CommentResponseDto;
import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTests {

    private final CommentMapper mapper = new CommentMapper();

    private Post post(long id) {
        Post p = new Post();
        p.setId(id);
        return p;
    }

    private User user(long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    @Test
    void toDto_ShouldMapNonDeletedCommentCorrectly() {
        Post p = post(10L);
        User u = user(1L, "john");
        Comment c = new Comment();
        c.setId(100L);
        c.setPost(p);
        c.setCreator(u);
        c.setContent("Hello world");
        c.setLikesCount(5);
        c.setIsDeleted(false);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());

        CommentResponseDto dto = mapper.toDto(c);

        assertEquals(100L, dto.id());
        assertEquals(10L, dto.postId());
        assertEquals(1L, dto.creatorId());
        assertEquals("Hello world", dto.content());
        assertFalse(dto.deleted());
        assertEquals(5, dto.likesCount());
    }

    @Test
    void toDto_ShouldHideContent_WhenDeleted() {
        Post p = post(10L);
        User u = user(1L, "john");

        Comment c = new Comment();
        c.setId(200L);
        c.setPost(p);
        c.setCreator(u);
        c.setContent("SECRET");
        c.setIsDeleted(true);
        c.setDeletedAt(LocalDateTime.now());
        c.setDeletedBy(u);

        CommentResponseDto dto = mapper.toDto(c);

        assertNull(dto.content(), "Deleted comments should not expose content");
        assertTrue(dto.deleted());
        assertEquals(u.getId(), dto.deletedById());
        assertEquals(u.getUsername(), dto.deletedByUsername());
    }

    @Test
    void toDto_ShouldMapParentCorrectly() {
        Post p = post(10L);
        User u = user(1L, "john");

        Comment parent = new Comment();
        parent.setId(50L);
        parent.setPost(p);

        Comment child = new Comment();
        child.setId(51L);
        child.setPost(p);
        child.setCreator(u);
        child.setParent(parent);

        CommentResponseDto dto = mapper.toDto(child);

        assertEquals(50L, dto.parentId());
    }

    @Test
    void createFromDto_ShouldCreateValidComment() {
        Post p = post(10L);
        User u = user(1L, "john");
        CreateCommentDto dto = new CreateCommentDto("Hello!", null);

        Comment result = mapper.createFromDto(p, u, null, dto);

        assertEquals("Hello!", result.getContent());
        assertEquals(p, result.getPost());
        assertEquals(u, result.getCreator());
        assertEquals(0, result.getLikesCount());
        assertNull(result.getParent());
    }
}
