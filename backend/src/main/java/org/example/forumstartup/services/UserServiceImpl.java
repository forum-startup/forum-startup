package org.example.forumstartup.services;

import lombok.RequiredArgsConstructor;
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
import org.example.forumstartup.spec.UserSpecs;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.example.forumstartup.utils.StringConstants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationUtils authenticationUtils;

    @Override
    @Transactional(readOnly = true)
    public Long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", "id", id.toString()));
    }

    /*
        Admin only
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    /*
        Admin only
     */
    @Override
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    /*
        Admin only
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsersByFirstName(String firstName) {
        return userRepository.searchUserByFirstName(firstName);
    }

    /*
        Admin only
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User create(User user) {
        if (!isDuplicate(user)) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new EntityNotFoundException(USER_ROLE_NOT_FOUND));
            user.getRoles().add(userRole);

            user.setPassword(passwordEncoder.encode(user.getPassword()));

            return userRepository.save(user);
        }
        throw new DuplicateEntityException(DUPLICATE_USER_INFORMATION_EXCEPTION_MESSAGE);
    }

    /*
        Regular user self update
     */
    @Override
    @Transactional
    public User update(UserSelfUpdateDto dto) {
        User actingUser = authenticationUtils.getAuthenticatedUser();

        updateCommonFields(
                actingUser,
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.password(),
                dto.profilePhotoUrl()
        );

        return userRepository.saveAndFlush(actingUser);
    }

    /*
        Admin self update
     */
    @Override
    @Transactional
    public User update(AdminSelfUpdateDto dto) {
        User actingUser = authenticationUtils.getAuthenticatedUser();

        updateCommonFields(
                actingUser,
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.password(),
                dto.profilePhotoUrl()
        );

        if (dto.username() != null) {
            ensureUsernameAvailable(actingUser, dto.username());
            actingUser.setUsername(dto.username());
        }
        if (dto.phoneNumber() != null) actingUser.setPhoneNumber(dto.phoneNumber());

        return userRepository.saveAndFlush(actingUser);
    }

    /*
        Admin only, deletes any user
     */
    @Override
    @Transactional
    public void delete(Long id) {
        User actingUser = authenticationUtils.getAuthenticatedUser();

        if (!isAdmin(actingUser) && !actingUser.getId().equals(id)) {
            throw new AuthorizationException(YOU_ARE_NOT_ALLOWED_TO_DELETE_THIS_ACCOUNT);
        }

        User deleteUser;

        if (id != null) {
            deleteUser = getUserById(id);
        } else {
            deleteUser = actingUser;
        }


        userRepository.delete(deleteUser);
    }

    /*
        Self delete, allowed for regular users
     */
    @Override
    @Transactional
    public void deleteSelf() {
        User actingUser = authenticationUtils.getAuthenticatedUser();
        userRepository.delete(actingUser);
    }

    /*
        Admin only
     */
    @Override
    @Transactional
    public void block(Long id) {
        User user = getUserById(id);
        User actingAdmin = authenticationUtils.getAuthenticatedUser();

        if (id.equals(actingAdmin.getId())) {
            throw new InvalidOperationException(YOU_CANNOT_BLOCK_YOURSELF);
        }
        user.setBlocked(true);

        userRepository.saveAndFlush(user);
    }

    /*
        Admin only
     */
    @Override
    @Transactional
    public void unblock(Long id) {
        User user = getUserById(id);

        User actingAdmin = authenticationUtils.getAuthenticatedUser();

        if (id.equals(actingAdmin.getId())) {
            throw new InvalidOperationException(YOU_CANNOT_UNBLOCK_YOURSELF);
        }

        user.setBlocked(false);

        userRepository.saveAndFlush(user);
    }

    /*
        Admin only
     */
    @Override
    @Transactional
    public void promoteToAdmin(Long id) {
        User actingAdmin = authenticationUtils.getAuthenticatedUser();

        if (!isAdmin(actingAdmin)) {
            throw new AuthorizationException(ONLY_ADMINS_CAN_PROMOTE_USERS);
        }

        if (id.equals(actingAdmin.getId())) {
            throw new InvalidOperationException(YOU_CANNOT_PROMOTE_YOURSELF);
        }

        User targetUser = getUserById(id);

        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new EntityNotFoundException(ADMIN_ROLE_NOT_FOUND));
        targetUser.getRoles().add(adminRole);

        userRepository.saveAndFlush(targetUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> filterUsers(String searchQuery, Pageable pageable) {

        Specification<User> spec = UserSpecs.matchesAny(searchQuery);

        return userRepository.findAll(spec, pageable);
    }

    /* ------------------------- Helpers ------------------------- */

    private boolean isDuplicate(User user) {
        return userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail());
    }

    private boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(ERole.ROLE_ADMIN));
    }

    private void updateCommonFields(User user, String firstName, String lastName, String email, String password, String profilePhotoUrl) {
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (email != null) {
            ensureEmailAvailable(user, email);
            user.setEmail(email);
        }
        if (password != null) user.setPassword(passwordEncoder.encode(password));
        if (profilePhotoUrl != null) user.setProfilePhotoUrl(profilePhotoUrl);
    }

    private void ensureUsernameAvailable(User actingUser, String username) {
        if (!actingUser.getUsername().equals(username)
                && userRepository.existsByUsername(username)) {
            throw new DuplicateEntityException(USERNAME_ALREADY_EXISTS);
        }
    }

    private void ensureEmailAvailable(User actingUser, String email) {
        if (!actingUser.getEmail().equals(email)
                && userRepository.existsByEmail(email)) {
            throw new DuplicateEntityException(EMAIL_ALREADY_EXISTS);
        }
    }

}
