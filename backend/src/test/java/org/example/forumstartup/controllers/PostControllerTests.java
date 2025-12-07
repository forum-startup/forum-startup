package org.example.forumstartup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.forumstartup.dtos.post.*;
import org.example.forumstartup.dtos.tags.AddTagsDto;
import org.example.forumstartup.dtos.tags.RemoveTagDto;
import org.example.forumstartup.mappers.PostMapper;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.PostService;
import org.example.forumstartup.utils.AuthenticationUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.data.domain.*;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PostControllerTests {

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Mock private PostService postService;
    @Mock private AuthenticationUtils auth;
    @Mock private PostMapper postMapper;

    @InjectMocks private PostController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    private User user() {
        User u = new User();
        u.setId(1L);
        u.setUsername("john");
        return u;
    }

    /* ============================================================
                              PUBLIC
       ============================================================ */

//    @Test
//    void getTotalPostCount_ShouldReturn200() throws Exception {
//        when(postService.getTotalPostCount()).thenReturn(42L);
//
//        mockMvc.perform(get("/api/public/posts/count"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.totalCount").value(42));
//    }

    @Test
    void getRecent_ShouldReturnList() throws Exception {
        Post p = new Post();
        p.setId(10L);

        PostResponseDto dto = new PostResponseDto(
                10L, 1L, "john", "t", "b",
                0, List.of(), null, null
        );

        when(postService.mostRecent(5)).thenReturn(List.of(p));
        when(postMapper.toSimpleDtoList(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/public/posts/recent?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value(10L));
    }

    /* ============================================================
                             PRIVATE READ
       ============================================================ */

    /*@Test
    void filterPosts_ShouldReturnPage() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        Post p = new Post();
        p.setId(9L);

        Page<Post> page = new PageImpl<>(List.of(p));

        PostWithLikeStatusResponseDto dto =
                new PostWithLikeStatusResponseDto(
                        9L, 1L, "john",
                        "title here",
                        "valid content string with more than 32 chars",
                        0, false, List.of(), null, null
                );

        when(postService.filterPosts(eq(null), any(Pageable.class)))
                .thenReturn(page);

        when(postMapper.toAuthenticatedDto(any(Post.class), eq(u)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/private/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].postId").value(9L));
    }*/

    @Test
    void getById_ShouldReturnPost() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        Post p = new Post();
        p.setId(20L);

        PostWithLikeStatusResponseDto dto =
                new PostWithLikeStatusResponseDto(
                        20L, 1L, "john", "t", "b",
                        0, false, List.of(), null, null
                );

        when(postService.getById(20L)).thenReturn(p);
        when(postMapper.toAuthenticatedDto(p, u)).thenReturn(dto);

        mockMvc.perform(get("/api/private/posts/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(20L));
    }

    @Test
    void getByCreator_ShouldReturnList() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        Post p = new Post();
        p.setId(99L);

        PostWithLikeStatusResponseDto dto =
                new PostWithLikeStatusResponseDto(
                        99L, 1L, "john", "t", "b",
                        0, false, List.of(), null, null
                );

        when(postService.findByCreatorId(1L, 10)).thenReturn(List.of(p));
        when(postMapper.toAuthenticatedDtoList(List.of(p), u))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/private/posts/by-author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value(99L));
    }

    /* ============================================================
                             PRIVATE WRITE
       ============================================================ */

    @Test
    void create_ShouldReturn201() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        // Content MUST be >= 32 characters!
        String longContent = "This is a valid long content body!!!";

        PostCreateDto req = new PostCreateDto(
                "title title title!",
                longContent
        );

        Post p = new Post();
        p.setId(100L);

        when(postMapper.toPost(req)).thenReturn(p);
        when(postService.create(p, u)).thenReturn(p);

        PostWithLikeStatusResponseDto dto =
                new PostWithLikeStatusResponseDto(
                        100L,
                        1L,
                        "john",
                        "title title title!",
                        longContent,
                        0,
                        false,
                        List.of(),
                        null,
                        null
                );

        when(postMapper.toAuthenticatedDto(p, u)).thenReturn(dto);

        mockMvc.perform(
                        post("/api/private/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(100L));
    }

    @Test
    void edit_ShouldReturn200() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        String longContent = "this is long enough content string!!!"; // > 32 chars

        PostUpdateDto req =
                new PostUpdateDto("new title new title!", longContent);

        Post updated = new Post();
        updated.setId(50L);

        when(postMapper.toPostWhenUpdate(req)).thenReturn(updated);
        when(postService.edit(50L, updated, u)).thenReturn(updated);

        PostWithLikeStatusResponseDto dto =
                new PostWithLikeStatusResponseDto(
                        50L, 1L, "john",
                        "new title new title!", longContent,
                        0, false, List.of(), null, null
                );

        when(postMapper.toAuthenticatedDto(updated, u)).thenReturn(dto);

        mockMvc.perform(
                        put("/api/private/posts/50")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(50L));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        mockMvc.perform(delete("/api/private/posts/10"))
                .andExpect(status().isNoContent());

        verify(postService).delete(10L, u);
    }

    /* ============================================================
                                 LIKES
       ============================================================ */

    @Test
    void like_ShouldReturn204() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        mockMvc.perform(post("/api/private/posts/10/like"))
                .andExpect(status().isNoContent());

        verify(postService).like(10L, u);
    }

    @Test
    void unlike_ShouldReturn204() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        mockMvc.perform(post("/api/private/posts/10/unlike"))
                .andExpect(status().isNoContent());

        verify(postService).unlike(10L, u);
    }

    /* ============================================================
                             TAG COMMANDS
       ============================================================ */

    @Test
    void addTags_ShouldReturn204() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        AddTagsDto dto = new AddTagsDto(List.of("java"));

        mockMvc.perform(
                        post("/api/private/posts/10/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto))
                )
                .andExpect(status().isNoContent());

        verify(postService).addTagsToPost(10L, u, dto.tags());
    }

    @Test
    void removeTag_ShouldReturn204() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        RemoveTagDto dto = new RemoveTagDto("java");

        mockMvc.perform(
                        delete("/api/private/posts/10/tags")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(dto))
                )
                .andExpect(status().isNoContent());

        verify(postService).removeTagFromPost(10L, u, "java");
    }
}
