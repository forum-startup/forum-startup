package org.example.forumstartup.utils;

import org.example.forumstartup.dtos.CreateAdminUserDto;
import org.example.forumstartup.dtos.RegisterUserDto;
import org.example.forumstartup.dtos.UserResponseDto;
import org.example.forumstartup.dtos.UserResponseDtoForAdmin;
import org.example.forumstartup.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /*
        TODO: think about profile picture insertion
     */

    public User registerDtoToUser(RegisterUserDto dto) {
        return new User(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.username(),
                dto.password()
        );
    }

    public User createAdminFromDto(CreateAdminUserDto dto) {
        return new User(
                dto.firstName(),
                dto.lastName(),
                dto.email(),
                dto.username(),
                dto.password(),
                dto.phoneNumber()
        );
    }

    public UserResponseDtoForAdmin userToResponseDtoForAdmin(User user) {
        return new UserResponseDtoForAdmin(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getRoles(),
                user.isBlocked()
        );
    }

    public UserResponseDto userToResponseDto(User user) {
        return new UserResponseDto(
                user.getUsername(),
                user.getRoles()
        );
    }
}
