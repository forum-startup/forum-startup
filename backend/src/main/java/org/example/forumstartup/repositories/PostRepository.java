package org.example.forumstartup.repositories;

import org.example.forumstartup.models.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByCreatorId(Long creatorId, Sort sort);

    List<Post> findTop12ByOrderByCreatedAtDesc();

    @Query("""
            SELECT p FROM Post p
            WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :word, '%'))
               OR LOWER(p.content) LIKE LOWER(CONCAT('%', :word, '%'))
            ORDER BY p.createdAt DESC
            """)
    List<Post> search(@Param("word") String word);

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN p.comments c
            GROUP BY p
            ORDER BY COUNT(c.id) DESC, p.createdAt DESC
            """)
    List<Post> findTop10MostCommented();

    @Query("""
            SELECT p FROM Post p JOIN p.tags t WHERE t.name= :tagName
            ORDER BY p.createdAt DESC 
                        """)
    List<Post> findPostsByTagName(@Param("tagName") String name);

    long count();
}