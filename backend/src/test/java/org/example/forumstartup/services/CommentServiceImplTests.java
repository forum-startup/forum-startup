package org.example.forumstartup.services;

import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.dtos.comment.UpdateCommentDto;
import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.mappers.CommentMapper;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.Role;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.CommentRepository;
import org.example.forumstartup.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTests {

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentMapper commentMapper;

    @InjectMocks
    CommentServiceImpl commentService;

    Post post;
    User user;
    User admin;

    @BeforeEach
    void init() {
        post = new Post();
        post.setId(10L);

        user = new User();
        user.setId(1L);
        user.setBlocked(false);
        user.setRoles(Set.of(role(ERole.ROLE_USER)));

        admin = new User();
        admin.setId(2L);
        admin.setBlocked(false);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));
    }

    /* ========================= CREATE COMMENT ========================= */

    @Test
    void createComment_ShouldCreate_WhenValid() {
        CreateCommentDto dto = new CreateCommentDto("Hello world", null);
        Comment mapped = new Comment();
        mapped.setContent("Hello world");

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(commentMapper.createFromDto(post, user, null, dto)).thenReturn(mapped);
        when(commentRepository.save(any())).thenReturn(mapped);

        Comment result = commentService.createComment(10L, user, dto);

        assertEquals("Hello world", result.getContent());
        verify(commentRepository).save(mapped);
    }

    @Test
    void createComment_ShouldCreateReply_WhenParentValid() {
        CreateCommentDto dto = new CreateCommentDto("Reply text", 111L);

        Comment parent = new Comment();
        parent.setId(111L);
        parent.setPost(post);

        Comment mapped = new Comment();
        mapped.setContent("Reply text");

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(111L)).thenReturn(Optional.of(parent));
        when(commentMapper.createFromDto(post, user, parent, dto)).thenReturn(mapped);
        when(commentRepository.save(mapped)).thenReturn(mapped);

        Comment result = commentService.createComment(10L, user, dto);

        assertEquals("Reply text", result.getContent());
        verify(commentRepository).save(mapped);
    }

    @Test
    void createComment_ShouldThrow_WhenParentInDifferentPost() {
        CreateCommentDto dto = new CreateCommentDto("Reply text", 111L);

        Post otherPost = new Post();
        otherPost.setId(99L);

        Comment parent = new Comment();
        parent.setId(111L);
        parent.setPost(otherPost);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(commentRepository.findById(111L)).thenReturn(Optional.of(parent));

        assertThrows(AuthorizationException.class,
                () -> commentService.createComment(10L, user, dto));
    }

    @Test
    void createComment_ShouldThrow_WhenUserBlocked() {
        user.setBlocked(true);
        CreateCommentDto dto = new CreateCommentDto("Test", null);

        assertThrows(AuthorizationException.class,
                () -> commentService.createComment(10L, user, dto));
    }

    /* ========================= UPDATE COMMENT ========================= */

    @Test
    void updateComment_ShouldUpdate_WhenOwner() {
        UpdateCommentDto dto = new UpdateCommentDto("Updated text");

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setCreator(user);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.updateComment(100L, user, dto);

        assertEquals("Updated text", result.getContent());
        verify(commentRepository).save(comment);
    }

    @Test
    void updateComment_ShouldUpdate_WhenAdmin() {
        UpdateCommentDto dto = new UpdateCommentDto("Admin update");

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setCreator(user); // not admin

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.updateComment(100L, admin, dto);

        assertEquals("Admin update", result.getContent());
    }

    @Test
    void updateComment_ShouldThrow_WhenUnauthorized() {
        User stranger = new User();
        stranger.setId(99L);
        stranger.setRoles(Set.of(role(ERole.ROLE_USER)));

        Comment comment = new Comment();
        comment.setId(100L);
        comment.setCreator(user);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(comment));

        assertThrows(AuthorizationException.class,
                () -> commentService.updateComment(100L, stranger, new UpdateCommentDto("x")));
    }

    /* ========================= SOFT DELETE ========================= */

    @Test
    void softDeleteComment_ShouldWork_WhenOwner() {
        Comment c = new Comment();
        c.setId(100L);
        c.setCreator(user);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));

        commentService.softDeleteComment(100L, user);

        assertTrue(c.getIsDeleted());
        assertNotNull(c.getDeletedAt());
        assertEquals(user, c.getDeletedBy());
    }

    @Test
    void softDeleteComment_ShouldWork_WhenAdmin() {
        Comment c = new Comment();
        c.setId(100L);
        c.setCreator(user);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));

        commentService.softDeleteComment(100L, admin);

        assertTrue(c.getIsDeleted());
        assertEquals(admin, c.getDeletedBy());
    }

    @Test
    void softDeleteComment_ShouldThrow_WhenUnauthorized() {
        User stranger = new User();
        stranger.setId(99L);
        stranger.setRoles(Set.of(role(ERole.ROLE_USER)));

        Comment c = new Comment();
        c.setId(100L);
        c.setCreator(user);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));

        assertThrows(AuthorizationException.class,
                () -> commentService.softDeleteComment(100L, stranger));
    }

    /* ========================= LIKE COMMENT ========================= */

    @Test
    void likeComment_ShouldIncreaseLikes() {
        Comment c = new Comment();
        c.setId(100L);
        c.setCreator(user);
        c.setLikesCount(0);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));
        when(commentRepository.save(c)).thenReturn(c);

        Comment result = commentService.likeComment(100L, admin);

        assertEquals(1, result.getLikesCount());
        assertTrue(result.getLikedBy().contains(admin));
    }

    @Test
    void likeComment_ShouldThrow_WhenLikingOwnComment() {
        Comment c = new Comment();
        c.setId(100L);
        c.setCreator(user);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));

        assertThrows(AuthorizationException.class,
                () -> commentService.likeComment(100L, user));
    }

    @Test
    void likeComment_ShouldThrow_WhenUserBlocked() {
        user.setBlocked(true);

        assertThrows(AuthorizationException.class,
                () -> commentService.likeComment(100L, user));
    }

    /* ========================= UNLIKE COMMENT ========================= */

    @Test
    void unlikeComment_ShouldDecrease_WhenLikedBefore() {
        Comment c = new Comment();
        c.setId(100L);
        c.setLikesCount(1);
        c.getLikedBy().add(admin);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));

        Comment result = commentService.unlikeComment(100L, admin);

        assertEquals(0, result.getLikesCount());
        assertFalse(result.getLikedBy().contains(admin));
    }

    @Test
    void unlikeComment_ShouldNotChange_WhenNotLiked() {
        Comment c = new Comment();
        c.setId(100L);
        c.setLikesCount(0);

        when(commentRepository.findById(100L)).thenReturn(Optional.of(c));

        Comment result = commentService.unlikeComment(100L, admin);

        assertEquals(0, result.getLikesCount());
    }

    /* ========================= LIST COMMENTS ========================= */

    @Test
    void listCommentsByPost_ShouldReturnOrderedList() {
        Comment c1 = new Comment();
        Comment c2 = new Comment();

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdOrderByCreatedAtAsc(10L))
                .thenReturn(List.of(c1, c2));

        List<Comment> result = commentService.listCommentsByPost(10L);

        assertEquals(2, result.size());
    }

    @Test
    void listPaginated_ShouldReturnPage() {
        Page<Comment> page = new PageImpl<>(List.of(new Comment()));

        when(commentRepository.findByPostId(eq(10L), any(Pageable.class)))
                .thenReturn(page);

        Page<Comment> result = commentService.listCommentsByPost(10L, Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
    }

    /* ========================= HELPERS ========================= */

    private Role role(ERole name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }
}
