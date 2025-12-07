package org.example.forumstartup.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.example.forumstartup.utils.PageableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Tag(name = "Users")
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;
    private final AuthenticationUtils authenticationUtils;

    /* ------------------------- Public part ------------------------- */

    @Operation(
            summary = "Get total users count"
    )
    @GetMapping("/public/users/count")
    public ResponseEntity<?> getTotalUserCount() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.longToUserTotalCountResponseDto(userService.getTotalUserCount()));
    }

    /* ------------------------- Private part ------------------------- */

    @Operation(
            summary = "Get user profile"
    )
    @GetMapping("/private/users/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProfile() {
        User actingUser = authenticationUtils.getAuthenticatedUser();
        ProfileResponseDto response = mapper.userToProfileResponseDto(actingUser);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(
            summary = "Self update user information"
    )
    @PutMapping("/private/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUserSelf(@RequestBody @Valid UserSelfUpdateDto dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.userToResponseDto(userService.update(dto)));
    }

    @Operation(
            summary = "Delete user"
    )
    @DeleteMapping("/private/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteSelf() {
        userService.deleteSelf();
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    /* ------------------------- Admin part ------------------------- */

    @Operation(
            summary = "Get user by id",
            description = "Admin get any user"
    )
    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDtoForAdmin> getUserById(@PathVariable Long id) {
        UserResponseDtoForAdmin dto = mapper.userToResponseDtoForAdmin(userService.getUserById(id));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @Operation(
            summary = "Filter users",
            description = "Admin filter users by username, first name or email"
    )
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDtoForAdmin>> filterUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username,asc") String sort,
            @RequestParam(required = false) String searchQuery
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(PageableUtils.parseSort(sort)));

        Page<User> users = userService.filterUsers(searchQuery, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users.map(mapper::userToResponseDtoForAdmin));
    }

    @Operation(
            summary = "Self update admin user "
    )
    @PutMapping("/admin/update/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdminSelf(@RequestBody @Valid AdminSelfUpdateDto dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mapper.userToResponseDto(userService.update(dto)));
    }

    @Operation(
            summary = "Delete user",
            description = "Admin delete any user"
    )
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.delete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Block user",
            description = "Admin block any user"
    )
    @PutMapping("/admin/users/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> block(@PathVariable Long id) {
        userService.block(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Unblock user",
            description = "Admin unblock any user"
    )
    @PutMapping("/admin/users/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> unblock(@PathVariable Long id) {
        userService.unblock(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(
            summary = "Promote user",
            description = "Admin promote any user to admin"
    )
    @PutMapping("/admin/users/{id}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<?> promoteToAdmin(@PathVariable Long id) {
        userService.promoteToAdmin(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
