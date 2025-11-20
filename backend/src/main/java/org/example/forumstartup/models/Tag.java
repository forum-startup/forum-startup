package org.example.forumstartup.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

import static org.example.forumstartup.utils.StringConstants.TAG_NAME_CONSTRAINT_MESSAGE;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank(message = TAG_NAME_CONSTRAINT_MESSAGE)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @PrePersist
    @PreUpdate
    public void normalizeName() {
        this.name = this.name.toLowerCase();
    }
}
