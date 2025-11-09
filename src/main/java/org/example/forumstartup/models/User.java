package org.example.forumstartup.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.forumstartup.enums.Role;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.example.forumstartup.utils.StringConstants.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    @Size(min = 4, max = 32, message = FIRST_NAME_SIZE_CONSTRAINT_MESSAGE)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @Size(min = 4, max = 32, message = LAST_NAME_SIZE_CONSTRAINT_MESSAGE)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = EMAIL_TYPE_CONSTRAINT_MESSAGE)
    @NotBlank
    private String email;

    @Column(name = "username", updatable = false, nullable = false, unique = true)
    @NotBlank
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    // nullable by default
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    // set to false by default
    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked = false;

    @ManyToMany(mappedBy = "likedBy", fetch = FetchType.LAZY)
    private Set<Post> likedPosts = new HashSet<>();

    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    private Set<Post> createdPosts = new HashSet<>();

    @ManyToMany(mappedBy = "likedBy", fetch = FetchType.LAZY)
    private Set<Comment> likedComments = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // will automatically set createdAt to current time
    // when an entity is about to be created in the DB
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
