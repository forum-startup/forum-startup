package org.example.forumstartup.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.user.AdminSelfUpdateDto;
import org.example.forumstartup.dtos.user.ProfileResponseDto;
import org.example.forumstartup.dtos.user.UserResponseDtoForAdmin;
import org.example.forumstartup.dtos.user.UserSelfUpdateDto;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.User;
import org.example.forumstartup.services.UserService;
import org.example.forumstartup.mappers.UserMapper;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;
    private final AuthenticationUtils authenticationUtils;

    /* ------------------------- User part ------------------------- */

    @GetMapping("/private/users/profile")
    public ResponseEntity<?> getProfile() {
        try {
            User actingUser = authenticationUtils.getAuthenticatedUser();
            ProfileResponseDto response = mapper.userToProfileResponseDto(actingUser);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    @PutMapping("/private/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserSelf(@RequestBody @Valid UserSelfUpdateDto dto) {
        return ResponseEntity.ok(mapper.userToResponseDto(userService.update(dto)));
    }

    @DeleteMapping("/private/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteSelf() {
        userService.deleteSelf();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /* ------------------------- Admin part ------------------------- */

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDtoForAdmin> getUserById(@PathVariable Long id) {
        UserResponseDtoForAdmin dto = mapper.userToResponseDtoForAdmin(userService.getUserById(id));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/users/username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDtoForAdmin> getUserByUsername(@PathVariable String username) {
        UserResponseDtoForAdmin dto = mapper.userToResponseDtoForAdmin(
                userService.getUserByUsername(username)
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/users/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDtoForAdmin> getUserByEmail(@PathVariable String email) {
        UserResponseDtoForAdmin dto = mapper.userToResponseDtoForAdmin(
                userService.getUserByEmail(email)
        );

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/users/firstname/{firstName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDtoForAdmin>> searchUsersByFirstName(@PathVariable String firstName) {
        List<UserResponseDtoForAdmin> dto = userService.searchUsersByFirstName(firstName)
                .stream()
                .map(mapper::userToResponseDtoForAdmin)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDtoForAdmin>> getAll() {
        List<UserResponseDtoForAdmin> dto = userService.getAll()
                .stream()
                .map(mapper::userToResponseDtoForAdmin)
                .toList();

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/admin/update/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminSelf(@RequestBody @Valid AdminSelfUpdateDto dto) {
        return ResponseEntity.ok(mapper.userToResponseDto(userService.update(dto)));
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> block(@PathVariable Long id) {
        userService.block(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> unblock(@PathVariable Long id) {
        userService.unblock(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/users/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> promoteToAdmin(@PathVariable Long id) {
        userService.promoteToAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
