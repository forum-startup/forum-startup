package org.example.forumstartup.services;

import org.example.forumstartup.enums.Role;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.DuplicateEntityException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.forumstartup.utils.StringConstants.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getUserById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user", "id", id.toString()));
    }

    /*
        TODO:
        Instead of manually passing User user to authorize,
        use @PreAuthorize("hasRole('ADMIN')") from Spring Security
     */
    @Override
    public User getUserByUsername(String username, User actingUser) {
        requireAdmin(actingUser);

        return repository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User", "username", username));
    }

    @Override
    public User getUserByEmail(String email, User actingUser) {
        requireAdmin(actingUser);

        return repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    public List<User> searchUsersByFirstName(String firstName, User actingUser) {
        requireAdmin(actingUser);

        return repository.searchUserByFirstName(firstName);
    }

    @Override
    public List<User> getAll(User actingUser) {
        requireAdmin(actingUser);
        return repository.findAll();
    }

    /*
        TODO:
        Add RegisterUserDto with caution to what fields are exposed
     */
    @Override
    public User create(User user) {
        if (!isDuplicate(user)) {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            return repository.save(user);
        }
        throw new DuplicateEntityException(DUPLICATE_USER_INFORMATION_EXCEPTION_MESSAGE);
    }

    /*
        TODO:
        Add AdminUpdateUserDto, UserSelfUpdateDto
     */
    @Override
    public User update(Long id, User userUpdates, User actingUser) {
        User user = getUserById(id);

        boolean admin = isAdmin(actingUser);

        // Permission check
        if (!admin && !actingUser.getId().equals(id)) {
            throw new AuthorizationException(UNAUTHORIZED_ACTION_EXCEPTION_MESSAGE);
        }

        // Admin update
        if (admin) {
            if (userUpdates.getFirstName() != null)
                user.setFirstName(userUpdates.getFirstName());

            if (userUpdates.getLastName() != null)
                user.setLastName(userUpdates.getLastName());

            if (userUpdates.getUsername() != null) {
                ensureUsernameAvailable(user, userUpdates);
                user.setUsername(userUpdates.getUsername());
            }

            if (userUpdates.getRole() != null)
                user.setRole(userUpdates.getRole());

            if (userUpdates.getPhoneNumber() != null)
                user.setPhoneNumber(userUpdates.getPhoneNumber());

            if (userUpdates.getProfilePhotoUrl() != null)
                user.setProfilePhotoUrl(userUpdates.getProfilePhotoUrl());
        }

        // Regular user update
        else {
            if (userUpdates.getFirstName() != null)
                user.setFirstName(userUpdates.getFirstName());

            if (userUpdates.getLastName() != null)
                user.setLastName(userUpdates.getLastName());

            if (userUpdates.getEmail() != null) {
                ensureEmailAvailable(user, userUpdates);
                user.setEmail(userUpdates.getEmail());
            }

            if (userUpdates.getProfilePhotoUrl() != null)
                user.setProfilePhotoUrl(userUpdates.getProfilePhotoUrl());

            if (userUpdates.getPasswordHash() != null) {
                user.setPasswordHash(passwordEncoder.encode(userUpdates.getPasswordHash()));
            }
        }

        return repository.saveAndFlush(user);
    }

    @Override
    public void delete(Long id, User actingUser) {
        User user = getUserById(id);

        boolean admin = isAdmin(actingUser);

        // Permission check
        if (!admin && !actingUser.getId().equals(id)) {
            throw new AuthorizationException(UNAUTHORIZED_ACTION_EXCEPTION_MESSAGE);
        }

        repository.delete(user);
    }

    @Override
    public void block(Long id, User actingUser) {
        User user = getUserById(id);
        requireAdmin(actingUser);
        user.setBlocked(true);

        repository.saveAndFlush(user);
    }

    @Override
    public void unblock(Long id, User actingUser) {
        User user = getUserById(id);
        requireAdmin(actingUser);
        user.setBlocked(false);

        repository.saveAndFlush(user);
    }

    private boolean isAdmin(User user) {
        return user.getRole() == Role.ADMIN;
    }

    private void requireAdmin(User actingUser) {
        if (!isAdmin(actingUser)) {
            throw new AuthorizationException(UNAUTHORIZED_ACTION_EXCEPTION_MESSAGE);
        }
    }

    private boolean isDuplicate(User user) {
        return repository.existsByUsernameOrEmail(user.getUsername(), user.getEmail());
    }

    private void ensureUsernameAvailable(User user, User userUpdates) {
        if (!user.getUsername().equals(userUpdates.getUsername())
                && repository.existsByUsername(userUpdates.getUsername())) {
            throw new DuplicateEntityException(USERNAME_ALREADY_EXISTS);
        }
    }

    private void ensureEmailAvailable(User user, User userUpdates) {
        if (!user.getEmail().equals(userUpdates.getEmail())
                && repository.existsByEmail(userUpdates.getEmail())) {
            throw new DuplicateEntityException(EMAIL_ALREADY_EXISTS);
        }
    }

}
