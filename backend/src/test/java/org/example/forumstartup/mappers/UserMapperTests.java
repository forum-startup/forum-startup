package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.auth.RegisterUserDto;
import org.example.forumstartup.dtos.user.*;
import org.example.forumstartup.models.Role;
import org.example.forumstartup.models.User;
import org.example.forumstartup.enums.ERole;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTests {

    UserMapper mapper = new UserMapper();

    @Test
    void registerDtoToUser_ShouldMapCorrectly() {
        RegisterUserDto dto = new RegisterUserDto(
                "John", "Doe", "john@mail.com", "john123", "pass"
        );

        User user = mapper.registerDtoToUser(dto);

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@mail.com", user.getEmail());
        assertEquals("john123", user.getUsername());
        assertEquals("pass", user.getPassword());
    }

    @Test
    void userToResponseDto_ShouldMapCorrectly() {
        User u = new User();
        u.setId(5L);
        u.setUsername("john");
        u.setRoles(Set.of(role(ERole.ROLE_USER)));

        UserResponseDto dto = mapper.userToResponseDto(u);

        assertEquals(5L, dto.id());
        assertEquals("john", dto.username());
        assertEquals(1, dto.roles().size());
    }

    @Test
    void userToResponseDtoForAdmin_ShouldMapCorrectly() {
        User u = new User();
        u.setId(5L);
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setEmail("a@b.com");
        u.setUsername("john");
        u.setBlocked(true);
        u.setRoles(Set.of(role(ERole.ROLE_USER)));
        u.setCreatedAt(LocalDateTime.now());

        UserResponseDtoForAdmin dto = mapper.userToResponseDtoForAdmin(u);

        assertEquals(5L, dto.id());
        assertEquals("John", dto.firstName());
        assertTrue(dto.isBlocked());
    }

    @Test
    void userToProfileResponseDto_ShouldMapCorrectly() {
        User u = new User();
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setEmail("a@b.com");
        u.setUsername("john");
        u.setProfilePhotoUrl("pic.png");
        u.setCreatedAt(LocalDateTime.now());

        ProfileResponseDto dto = mapper.userToProfileResponseDto(u);

        assertEquals("John", dto.firstName());
        assertEquals("pic.png", dto.profilePhotoUrl());
    }

    private Role role(ERole name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }
}
