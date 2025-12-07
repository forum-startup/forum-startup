package org.example.forumstartup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.forumstartup.dtos.comment.CommentResponseDto;
import org.example.forumstartup.dtos.comment.CreateCommentDto;
import org.example.forumstartup.dtos.comment.UpdateCommentDto;
import org.example.forumstartup.mappers.CommentMapper;
import org.example.forumstartup.models.Comment;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.CommentService;
import org.example.forumstartup.utils.AuthenticationUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTests {

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Mock private CommentService commentService;
    @Mock private AuthenticationUtils authenticationUtils;
    @Mock private CommentMapper commentMapper;

    @InjectMocks private CommentController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    private User mockUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("john");
        return u;
    }

    /* ============================================================
                       LIST COMMENTS
       ============================================================ */

    /*@Test
    void listByPost_ShouldReturnPage() throws Exception {
        Comment c = new Comment();
        c.setId(1L);

        CommentResponseDto dto = new CommentResponseDto(
                1L, 10L, 1L, "john", "hello",
                null, 0, false, null, null, null, null, null
        );

        Page<Comment> page = new PageImpl<>(List.of(c));

        when(commentService.listCommentsByPost(eq(10L), any(Pageable.class)))
                .thenReturn(page);

        // IMPORTANT — exact match, NOT any()
        when(commentMapper.toDto(c)).thenReturn(dto);

        mockMvc.perform(get("/api/private/posts/10/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }*/

    /* ============================================================
                          CREATE COMMENT
       ============================================================ */

    @Test
    void createComment_ShouldReturn201() throws Exception {
        User user = mockUser();
        when(authenticationUtils.getAuthenticatedUser()).thenReturn(user);

        CreateCommentDto req = new CreateCommentDto("hello", null);

        Comment saved = new Comment();
        saved.setId(5L);

        CommentResponseDto dto = new CommentResponseDto(
                5L, 10L, 1L, "john", "hello",
                null, 0, false, null, null, null, null, null
        );

        when(commentService.createComment(eq(10L), eq(user), any()))
                .thenReturn(saved);

        when(commentMapper.toDto(saved)).thenReturn(dto);

        mockMvc.perform(
                        post("/api/private/posts/10/comments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L));
    }

    /* ============================================================
                          UPDATE COMMENT
       ============================================================ */

    @Test
    void updateComment_ShouldReturn200() throws Exception {
        User user = mockUser();
        when(authenticationUtils.getAuthenticatedUser()).thenReturn(user);

        UpdateCommentDto req = new UpdateCommentDto("updated");

        Comment updated = new Comment();
        updated.setId(7L);

        CommentResponseDto dto = new CommentResponseDto(
                7L, 10L, 1L, "john", "updated",
                null, 0, false, null, null, null, null, null
        );

        when(commentService.updateComment(eq(7L), eq(user), any()))
                .thenReturn(updated);

        when(commentMapper.toDto(updated)).thenReturn(dto);

        mockMvc.perform(
                        put("/api/private/comments/7")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L));
    }

    /* ============================================================
                         SOFT DELETE COMMENT
       ============================================================ */

    @Test
    void softDelete_ShouldReturn204() throws Exception {
        User user = mockUser();
        when(authenticationUtils.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(delete("/api/private/comments/10"))
                .andExpect(status().isNoContent());

        verify(commentService).softDeleteComment(10L, user);
    }

    /* ============================================================
                         ADMIN DELETE COMMENT
       ============================================================ */

    @Test
    void adminSoftDelete_ShouldReturn204() throws Exception {
        User admin = mockUser();
        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);

        mockMvc.perform(delete("/api/admin/comments/10"))
                .andExpect(status().isNoContent());

        verify(commentService).softAdminDeleteComment(10L, admin);
    }

    /* ============================================================
                            LIKE COMMENT
       ============================================================ */

    @Test
    void likeComment_ShouldReturn200() throws Exception {
        User user = mockUser();
        when(authenticationUtils.getAuthenticatedUser()).thenReturn(user);

        Comment c = new Comment();
        c.setId(3L);

        CommentResponseDto dto = new CommentResponseDto(
                3L, 10L, 1L, "john", "text",
                null, 1, false, null, null, null, null, null
        );

        when(commentService.likeComment(3L, user)).thenReturn(c);
        when(commentMapper.toDto(c)).thenReturn(dto);

        mockMvc.perform(post("/api/private/comments/3/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }

    /* ============================================================
                           UNLIKE COMMENT
       ============================================================ */

    @Test
    void unlikeComment_ShouldReturn200() throws Exception {
        User user = mockUser();
        when(authenticationUtils.getAuthenticatedUser()).thenReturn(user);

        Comment c = new Comment();
        c.setId(3L);

        CommentResponseDto dto = new CommentResponseDto(
                3L, 10L, 1L, "john", "text",
                null, 0, false, null, null, null, null, null
        );

        when(commentService.unlikeComment(3L, user)).thenReturn(c);
        when(commentMapper.toDto(c)).thenReturn(dto);

        mockMvc.perform(delete("/api/private/comments/3/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L));
    }
}
