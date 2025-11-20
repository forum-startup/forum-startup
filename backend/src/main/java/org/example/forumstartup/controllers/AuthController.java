package org.example.forumstartup.controllers;

import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.jwt.JwtResponseDto;
import org.example.forumstartup.dtos.auth.LoginUserDto;
import org.example.forumstartup.dtos.auth.RegisterUserDto;
import org.example.forumstartup.models.User;
import org.example.forumstartup.security.JwtUtils;
import org.example.forumstartup.services.UserService;
import org.example.forumstartup.mappers.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwt;
    private final UserService userService;
    private final UserMapper mapper;

    /*
            Passes username/password to UserDetailsService which returns a UserDetails object
            and checks the password against the PasswordEncoder

            BadCredentialsException -> wrong username or password
            DisabledException -> user is blocked
            LockedException -> account locked

    */

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto dto) {

        Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.username(), dto.password()));

        User user = userService.getUserByUsername(dto.username());

        String token = jwt.generateToken(
                user.getUsername(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
        );

        /*
            If login successful, return the token + username + roles to frontend
         */
        return ResponseEntity.ok(new JwtResponseDto(token, user.getUsername(), user.getRoles()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto dto) {
        User user = mapper.registerDtoToUser(dto);

        return ResponseEntity.ok(userService.create(user));
    }
}
