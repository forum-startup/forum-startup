package org.example.forumstartup.repositories;

import org.example.forumstartup.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    List<Comment> findByCreatorId(Long creatorId);

    Optional<Comment> findByIdAndCreatorId(Long id, Long creatorId);

    Page<Comment> findByPostId(Long postId, Pageable pageable);
}
