package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.user.CreateAdminUserDto;
import org.example.forumstartup.dtos.auth.RegisterUserDto;
import org.example.forumstartup.dtos.user.ProfileResponseDto;
import org.example.forumstartup.dtos.user.UserResponseDto;
import org.example.forumstartup.dtos.user.UserResponseDtoForAdmin;
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

    public ProfileResponseDto userToProfileResponseDto(User user) {
        return new ProfileResponseDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getProfilePhotoUrl(),
                user.getCreatedAt()
        );
    }
}
