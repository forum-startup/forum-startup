package org.example.forumstartup.services;

import org.example.forumstartup.dtos.user.AdminSelfUpdateDto;
import org.example.forumstartup.dtos.user.UserSelfUpdateDto;
import org.example.forumstartup.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    public Long getTotalUserCount();

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

    Page<User> filterUsers(String stringQuery, Pageable pageable);
}
