package org.example.forumstartup.services;

import org.example.forumstartup.dtos.user.AdminSelfUpdateDto;
import org.example.forumstartup.dtos.user.UserSelfUpdateDto;
import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.DuplicateEntityException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.exceptions.InvalidOperationException;
import org.example.forumstartup.models.Role;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.RoleRepository;
import org.example.forumstartup.repositories.UserRepository;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationUtils authenticationUtils;

    @InjectMocks private UserServiceImpl userService;

    User baseUser;

    @BeforeEach
    void init() {
        baseUser = new User();
        baseUser.setId(1L);
        baseUser.setUsername("john");
        baseUser.setEmail("john@example.com");
        baseUser.setPassword("encoded");
        baseUser.setRoles(new HashSet<>());
        baseUser.setBlocked(false);
    }

    // ================================================================
    // region: READ METHODS
    // ================================================================

    @Test
    void getTotalUserCount_returnsCount() {
        when(userRepository.count()).thenReturn(42L);
        Long result = userService.getTotalUserCount();
        assertEquals(42L, result);
        verify(userRepository).count();
    }

    @Test
    void getUserById_returnsUser_whenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(baseUser));
        User u = userService.getUserById(1L);
        assertEquals(1L, u.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_throws_whenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getUserByUsername_returnsUser() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(baseUser));
        User u = userService.getUserByUsername("john");
        assertEquals("john", u.getUsername());
    }

    @Test
    void getUserByUsername_throws_whenNotFound() {
        when(userRepository.findByUsername("xx")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByUsername("xx"));
    }

    @Test
    void getUserByEmail_returnsUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(baseUser));
        User u = userService.getUserByEmail("john@example.com");
        assertEquals("john@example.com", u.getEmail());
    }

    @Test
    void getUserByEmail_throws_whenNotFound() {
        when(userRepository.findByEmail("xx@example.com")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail("xx@example.com"));
    }

    @Test
    void searchUsersByFirstName_returnsList() {
        User u1 = new User();
        User u2 = new User();
        when(userRepository.searchUserByFirstName("john"))
                .thenReturn(List.of(u1, u2));
        List<User> result = userService.searchUsersByFirstName("john");
        assertEquals(2, result.size());
    }

    @Test
    void getAll_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of(baseUser));
        List<User> result = userService.getAll();
        assertEquals(1, result.size());
    }

    // ================================================================
    // endregion READ METHODS
    // ================================================================



    // ================================================================
    // region: CREATE USER
    // ================================================================

    @Test
    void create_createsUser_whenNotDuplicate() {
        User newUser = new User();
        newUser.setUsername("alice");
        newUser.setEmail("alice@example.com");
        newUser.setPassword("pass");
        newUser.setRoles(new HashSet<>());

        Role userRole = new Role();
        userRole.setName(ERole.ROLE_USER);

        when(userRepository.existsByUsernameOrEmail("alice", "alice@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("pass")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        User created = userService.create(newUser);

        assertEquals("ENCODED", created.getPassword());
        assertTrue(created.getRoles().contains(userRole));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_throws_whenDuplicate() {
        User newUser = new User();
        newUser.setUsername("alice");
        newUser.setEmail("alice@example.com");
        newUser.setPassword("pass");
        newUser.setRoles(new HashSet<>());

        when(userRepository.existsByUsernameOrEmail("alice", "alice@example.com")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> userService.create(newUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_throws_whenUserRoleNotFound() {
        User newUser = new User();
        newUser.setUsername("alice");
        newUser.setEmail("alice@example.com");
        newUser.setPassword("pass");

        when(userRepository.existsByUsernameOrEmail("alice", "alice@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_USER)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.create(newUser));
    }

    // ================================================================
    // endregion CREATE USER
    // ================================================================



    // ================================================================
    // region: SELF UPDATE (UserSelfUpdateDto)
    // ================================================================

    @Test
    void updateSelf_updatesAllNonNullFields() {
        User acting = baseUser;

        UserSelfUpdateDto dto = new UserSelfUpdateDto(
                "NewFirst",
                "NewLast",
                "new@example.com",
                "http://pic.com",
                "newpass"
        );

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(acting);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpass")).thenReturn("ENC_NEW");

        User saved = new User();
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(saved);

        User result = userService.update(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        User updated = captor.getValue();

        assertEquals("NewFirst", updated.getFirstName());
        assertEquals("NewLast", updated.getLastName());
        assertEquals("new@example.com", updated.getEmail());
        assertEquals("ENC_NEW", updated.getPassword());
        assertEquals("http://pic.com", updated.getProfilePhotoUrl());
    }

    @Test
    void updateSelf_throws_whenEmailExists() {
        User acting = baseUser;

        UserSelfUpdateDto dto = new UserSelfUpdateDto(
                "NewFirst",
                "NewLast",
                "taken@example.com",
                "http://pic.com",
                "newpass"
        );

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(acting);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> userService.update(dto));
    }

    // ================================================================
    // endregion SELF UPDATE
    // ================================================================



    // ================================================================
    // region: ADMIN SELF UPDATE (AdminSelfUpdateDto)
    // ================================================================

    @Test
    void adminUpdate_updatesAllFields() {
        User acting = baseUser;

        AdminSelfUpdateDto dto = new AdminSelfUpdateDto(
                "A",
                "B",
                "admin@example.com",
                "newUsername",
                "pass",
                "55555",
                "http://photo"
        );

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(acting);

        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("ENC");

        User saved = new User();
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(saved);

        userService.update(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(captor.capture());
        User updated = captor.getValue();

        assertEquals("newUsername", updated.getUsername());
        assertEquals("55555", updated.getPhoneNumber());
        assertEquals("admin@example.com", updated.getEmail());
        assertEquals("ENC", updated.getPassword());
    }

    @Test
    void adminUpdate_throws_whenUsernameTaken() {
        User acting = baseUser;

        AdminSelfUpdateDto dto = new AdminSelfUpdateDto(
                "A","B","mail@example.com",
                "takenUser", "pass", "33333", "http://pic"
        );

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(acting);
        when(userRepository.existsByUsername("takenUser")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> userService.update(dto));
    }

    @Test
    void adminUpdate_throws_whenEmailTaken() {
        User acting = baseUser;

        AdminSelfUpdateDto dto = new AdminSelfUpdateDto(
                "A","B","mail@example.com",
                null, "pass","33333","pic"
        );

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(acting);
        when(userRepository.existsByEmail("mail@example.com")).thenReturn(true);

        assertThrows(DuplicateEntityException.class, () -> userService.update(dto));
    }

    // ================================================================
    // endregion ADMIN SELF UPDATE
    // ================================================================



    // ================================================================
    // region: DELETE + DELETE SELF
    // ================================================================

    @Test
    void delete_adminCanDeleteAnyUser() {
        User admin = new User();
        admin.setId(99L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        User victim = baseUser;

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(1L)).thenReturn(Optional.of(victim));

        userService.delete(1L);

        verify(userRepository).delete(victim);
    }

    @Test
    void delete_userCanDeleteSelf() {
        User acting = baseUser;

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(acting);
        when(userRepository.findById(1L)).thenReturn(Optional.of(acting));

        userService.delete(1L);

        verify(userRepository).delete(acting);
    }

    @Test
    void delete_throws_whenNonAdminTriesDeletingOtherUser() {
        User acting = baseUser;
        User other = new User(); other.setId(2L);

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(acting);

        assertThrows(AuthorizationException.class, () -> userService.delete(2L));
    }

    @Test
    void deleteSelf_deletesActingUser() {
        when(authenticationUtils.getAuthenticatedUser()).thenReturn(baseUser);

        userService.deleteSelf();

        verify(userRepository).delete(baseUser);
    }

    // ================================================================
    // endregion DELETE
    // ================================================================



    // ================================================================
    // region: BLOCK / UNBLOCK
    // ================================================================

    @Test
    void block_blocksUser_whenAdmin() {
        User admin = new User();
        admin.setId(99L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        User target = new User();
        target.setId(2L);
        target.setBlocked(false);

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        userService.block(2L);

        assertTrue(target.isBlocked());
        verify(userRepository).saveAndFlush(target);
    }

    @Test
    void block_throws_whenAdminBlocksSelf() {
        User admin = new User();
        admin.setId(1L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(InvalidOperationException.class, () -> userService.block(1L));
    }

    @Test
    void unblock_unblocksUser_whenAdmin() {
        User admin = new User();
        admin.setId(99L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        User target = new User();
        target.setId(2L);
        target.setBlocked(true);

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));

        userService.unblock(2L);

        assertFalse(target.isBlocked());
        verify(userRepository).saveAndFlush(target);
    }

    @Test
    void unblock_throws_whenAdminUnblocksSelf() {
        User admin = new User();
        admin.setId(1L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin));

        assertThrows(InvalidOperationException.class, () -> userService.unblock(1L));
    }

    // ================================================================
    // endregion BLOCK / UNBLOCK
    // ================================================================



    // ================================================================
    // region: PROMOTE TO ADMIN
    // ================================================================

    @Test
    void promote_promotesUserToAdmin() {
        User admin = new User();
        admin.setId(99L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        User target = new User();
        target.setId(2L);
        target.setRoles(new HashSet<>());

        Role adminRole = role(ERole.ROLE_ADMIN);

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(roleRepository.findByName(ERole.ROLE_ADMIN))
                .thenReturn(Optional.of(adminRole));

        userService.promoteToAdmin(2L);

        assertTrue(target.getRoles().contains(adminRole));
    }

    @Test
    void promote_throws_whenActingUserNotAdmin() {
        User nonAdmin = new User();
        nonAdmin.setId(1L);
        nonAdmin.setRoles(Set.of()); // no admin role

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(nonAdmin);

        assertThrows(AuthorizationException.class, () -> userService.promoteToAdmin(2L));
    }

    @Test
    void promote_throws_whenPromotingSelf() {
        User admin = new User();
        admin.setId(1L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);

        assertThrows(InvalidOperationException.class, () -> userService.promoteToAdmin(1L));
    }

    @Test
    void promote_throws_whenAdminRoleNotFound() {
        User admin = new User();
        admin.setId(99L);
        admin.setRoles(Set.of(role(ERole.ROLE_ADMIN)));

        User target = new User(); target.setId(2L);

        when(authenticationUtils.getAuthenticatedUser()).thenReturn(admin);
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(roleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.promoteToAdmin(2L));
    }

    // ================================================================
    // endregion PROMOTE
    // ================================================================



    // ================================================================
    // region: FILTER USERS
    // ================================================================

    @Test
    void filterUsers_callsRepositoryWithSpec() {
        Pageable pageable = mock(Pageable.class);
        Page<User> page = new PageImpl<>(List.of(baseUser));

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<User> result = userService.filterUsers("query", pageable);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
    }

    // ================================================================
    // endregion FILTER USERS
    // ================================================================



    // ================================================================
    // Helper
    // ================================================================

    private Role role(ERole name) {
        Role r = new Role();
        r.setName(name);
        return r;
    }
}
