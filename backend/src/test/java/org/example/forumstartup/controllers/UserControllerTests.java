package org.example.forumstartup.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.forumstartup.dtos.user.*;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.mappers.UserMapper;
import org.example.forumstartup.models.Role;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.UserService;
import org.example.forumstartup.utils.AuthenticationUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTests {

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Mock private UserService userService;
    @Mock private UserMapper mapperUser;
    @Mock private AuthenticationUtils auth;

    @InjectMocks private UserController controller;

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
                         PUBLIC: COUNT
       ============================================================ */

//    @Test
//    void getTotalUserCount_ShouldReturn200() throws Exception {
//        when(userService.getTotalUserCount()).thenReturn(55L);
//
//        mockMvc.perform(get("/api/public/users/count"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("55"));
//    }

    /* ============================================================
                         PRIVATE: PROFILE
       ============================================================ */

    @Test
    void getProfile_ShouldReturnProfileDto() throws Exception {
        User u = user();
        when(auth.getAuthenticatedUser()).thenReturn(u);

        ProfileResponseDto dto =
                new ProfileResponseDto("John", "Doe", "john@mail.com", "john", null, LocalDateTime.now());

        when(mapperUser.userToProfileResponseDto(u)).thenReturn(dto);

        mockMvc.perform(get("/api/private/users/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

//    @Test
//    void getProfile_InvalidToken_ShouldReturn401() throws Exception {
//        when(auth.getAuthenticatedUser()).thenThrow(new EntityNotFoundException("bad token"));
//
//        mockMvc.perform(get("/api/private/users/profile"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$").value("Invalid token"));
//    }

    /* ============================================================
                     PRIVATE: UPDATE SELF
       ============================================================ */

    @Test
    void updateUserSelf_ShouldReturn200() throws Exception {
        UserSelfUpdateDto req = new UserSelfUpdateDto(
                "John",
                "Doee", // FIXED: must be ≥ 4 chars
                "john@mail.com",
                "http://photo.com",
                "secret123"
        );

        User u = user();
        UserResponseDto dto = new UserResponseDto(1L, "john", Set.of());

        when(userService.update(req)).thenReturn(u);
        when(mapperUser.userToResponseDto(u)).thenReturn(dto);

        mockMvc.perform(
                        put("/api/private/users/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john"));
    }

    /* ============================================================
                        PRIVATE: DELETE SELF
       ============================================================ */

    @Test
    void deleteSelf_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/private/users/me"))
                .andExpect(status().isNoContent());

        verify(userService).deleteSelf();
    }

    /* ============================================================
                          ADMIN: GET BY ID
       ============================================================ */

    @Test
    void getUserById_ShouldReturnDto() throws Exception {
        UserResponseDtoForAdmin dto =
                new UserResponseDtoForAdmin(
                        10L, "John", "Doe", "j@mail.com",
                        "john", Set.of(), LocalDateTime.now(), false
                );

        when(userService.getUserById(10L)).thenReturn(new User());
        when(mapperUser.userToResponseDtoForAdmin(any(User.class))).thenReturn(dto);

        mockMvc.perform(get("/api/admin/users/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    /* ============================================================
                        ADMIN: FILTER USERS
       ============================================================ */

    /*@Test
    void filterUsers_ShouldReturnPage() throws Exception {
        User u = user();

        Page<User> page = new PageImpl<>(List.of(u));

        UserResponseDtoForAdmin dto =
                new UserResponseDtoForAdmin(
                        1L, "John", "Doe", "john@mail.com",
                        "john", Set.of(), LocalDateTime.now(), false
                );

        when(userService.filterUsers(eq(null), any(Pageable.class)))
                .thenReturn(page);

        when(mapperUser.userToResponseDtoForAdmin(any(User.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }*/

    /* ============================================================
                       ADMIN: UPDATE SELF
       ============================================================ */

    @Test
    void updateAdminSelf_ShouldReturn200() throws Exception {
        AdminSelfUpdateDto req =
                new AdminSelfUpdateDto(
                        "John",
                        "Doee", // FIXED
                        "john@mail.com",
                        "john",
                        "secret123",
                        "55555",
                        "http://photo.com"
                );

        User updated = user();
        UserResponseDto dto = new UserResponseDto(1L, "john", Set.of());

        when(userService.update(req)).thenReturn(updated);
        when(mapperUser.userToResponseDto(updated)).thenReturn(dto);

        mockMvc.perform(
                        put("/api/admin/update/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    /* ============================================================
                           ADMIN COMMANDS
       ============================================================ */

    @Test
    void adminDeleteUser_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/admin/users/10"))
                .andExpect(status().isNoContent());

        verify(userService).delete(10L);
    }

    @Test
    void block_ShouldReturn204() throws Exception {
        mockMvc.perform(put("/api/admin/users/10/block"))
                .andExpect(status().isNoContent());

        verify(userService).block(10L);
    }

    @Test
    void unblock_ShouldReturn204() throws Exception {
        mockMvc.perform(put("/api/admin/users/10/unblock"))
                .andExpect(status().isNoContent());

        verify(userService).unblock(10L);
    }

    @Test
    void promoteToAdmin_ShouldReturn204() throws Exception {
        mockMvc.perform(put("/api/admin/users/10/promote"))
                .andExpect(status().isNoContent());

        verify(userService).promoteToAdmin(10L);
    }
}
