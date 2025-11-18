package org.example.forumstartup.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.AdminSelfUpdateDto;
import org.example.forumstartup.dtos.UserResponseDtoForAdmin;
import org.example.forumstartup.dtos.UserSelfUpdateDto;
import org.example.forumstartup.services.UserService;
import org.example.forumstartup.utils.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;
    private final UserMapper mapper;

    /* ------------------------- User part ------------------------- */

    @PutMapping("/private/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserSelf(@RequestBody UserSelfUpdateDto dto) {
        return ResponseEntity.ok(mapper.userToResponseDto(userService.update(dto)));
    }

    @DeleteMapping("/private/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteSelf() {
        userService.deleteSelf();
        return ResponseEntity.noContent().build();
    }

    /* ------------------------- Admin part ------------------------- */

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
    public ResponseEntity<?> updateAdminSelf(@RequestBody AdminSelfUpdateDto dto) {
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
