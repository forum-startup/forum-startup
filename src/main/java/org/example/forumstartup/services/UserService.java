package org.example.forumstartup.services;

import org.example.forumstartup.models.User;

import java.util.List;

public interface UserService {

    User getUserById(Long id);

    User getUserByUsername(String username, User actingUser);

    User getUserByEmail(String email, User actingUser);

    List<User> searchUsersByFirstName(String firstName, User actingUser);

    List<User> getAll(User actingUser);

    User create(User userCreate);

    User update(Long id, User userUpdate, User actingUser);

    void delete(Long id, User actingUser);

    void block(Long id, User actingUser);

    void unblock(Long id, User actingUser);

}
