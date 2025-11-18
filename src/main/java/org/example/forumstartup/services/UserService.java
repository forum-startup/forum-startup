package org.example.forumstartup.services;

import org.example.forumstartup.dtos.AdminSelfUpdateDto;
import org.example.forumstartup.dtos.UserSelfUpdateDto;
import org.example.forumstartup.models.User;

import java.util.List;

public interface UserService {

    User getUserById(Long id);

    User getUserByUsername(String username);

    User getUserByEmail(String email);

    List<User> searchUsersByFirstName(String firstName);

    List<User> getAll();

    User create(User userCreate);

    User update(UserSelfUpdateDto dto);

    User update(AdminSelfUpdateDto dto);

    void delete(Long id);

    void deleteSelf();

    void block(Long id);

    void unblock(Long id);

    void promoteToAdmin(Long id);

}
