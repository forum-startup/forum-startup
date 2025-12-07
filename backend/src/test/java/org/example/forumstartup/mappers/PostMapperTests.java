package org.example.forumstartup.mappers;

import org.example.forumstartup.dtos.post.PostResponseDto;
import org.example.forumstartup.dtos.post.PostWithLikeStatusResponseDto;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.models.User;
import org.example.forumstartup.utils.AuthenticationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostMapperTests {

    @Mock
    AuthenticationUtils authUtils;

    PostMapper mapper;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        mapper = new PostMapper(authUtils);
    }

    private User user(long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    private Tag tag(String name) {
        Tag t = new Tag();
        t.setName(name);
        return t;
    }

    @Test
    void toSimpleDto_ShouldMapAllFields() {
        User creator = user(1L, "john");
        Post post = new Post();
        post.setId(10L);
        post.setCreator(creator);
        post.setTitle("Title");
        post.setContent("Content");
        post.setLikesCount(5);
        post.setTags(Set.of(tag("b"), tag("a"))); // Should be sorted
        post.setCreatedAt(LocalDateTime.now());

        PostResponseDto dto = mapper.toSimpleDto(post);

        assertEquals(10L, dto.postId());
        assertEquals("Title", dto.title());
        assertEquals("Content", dto.content());
        assertEquals(5, dto.likesCount());
        assertEquals(List.of("a", "b"), dto.tags());
    }

    @Test
    void toAuthenticatedDto_ShouldSetLikedStatusCorrectly() {
        User creator = user(1L, "john");
        User current = user(2L, "alice");

        Post post = new Post();
        post.setId(10L);
        post.setCreator(creator);
        post.setTitle("T");
        post.setContent("C");
        post.setLikesCount(1);
        post.getLikedBy().add(current);

        PostWithLikeStatusResponseDto dto = mapper.toAuthenticatedDto(post, current);

        assertTrue(dto.likedByCurrentUser());
        assertEquals(1, dto.likesCount());
    }

    @Test
    void toAuthenticatedDto_ShouldReturnFalse_WhenUserNotLiked() {
        User creator = user(1L, "john");
        User current = user(2L, "alice");

        Post post = new Post();
        post.setCreator(creator);

        PostWithLikeStatusResponseDto dto = mapper.toAuthenticatedDto(post, current);

        assertFalse(dto.likedByCurrentUser());
    }
}
