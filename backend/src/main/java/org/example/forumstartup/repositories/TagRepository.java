package org.example.forumstartup.repositories;

import org.example.forumstartup.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Long> {

    Optional<Tag> findByName(String name);

    boolean existsByName(String name);
}
