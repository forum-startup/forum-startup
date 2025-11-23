package org.example.forumstartup.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.forumstartup.dtos.jwt.JwtResponseDto;
import org.example.forumstartup.dtos.auth.LoginUserDto;
import org.example.forumstartup.dtos.auth.RegisterUserDto;
import org.example.forumstartup.models.User;
import org.example.forumstartup.security.JwtUtils;
import org.example.forumstartup.services.UserService;
import org.example.forumstartup.mappers.UserMapper;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowCredentials = "true"
)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwt;
    private final UserService userService;
    private final UserMapper mapper;
    private final AuthenticationUtils authenticationUtils;

    /*
            Passes username/password to UserDetailsService which returns a UserDetails object
            and checks the password against the PasswordEncoder

            BadCredentialsException -> wrong username or password
            DisabledException -> user is blocked
            LockedException -> account locked

    */
    @PostMapping("/public/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginUserDto dto) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.username(), dto.password())
        );

        User user = userService.getUserByUsername(dto.username());

        String token = jwt.generateToken(
                user.getUsername(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
        );

        /*
            HttpOnly Cookie, sets token inside client cookie
         */
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        /*
            If login successful, return cookie + username + roles to frontend
         */
        return ResponseEntity
                .ok()
                .header("Set-Cookie", cookie.toString())
                .build();
    }

    @PostMapping("/public/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto dto) {
        User user = mapper.registerDtoToUser(dto);

        return ResponseEntity.ok(userService.create(user));
    }

    @PostMapping("private/auth/logout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        /*
            Clear the JWT cookie by setting one that expires immediately
         */
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .build();
    }

    @GetMapping("/private/auth/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> me() {
        User actingUser = authenticationUtils.getAuthenticatedUser();

        JwtResponseDto response = new JwtResponseDto(
                actingUser.getUsername(),
                actingUser.getRoles()
        );

        return ResponseEntity.ok(response);
    }
}
