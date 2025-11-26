package org.example.forumstartup.services;

import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.Role;
import org.example.forumstartup.models.Tag;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private TagService tagService;

    @InjectMocks
    private PostServiceImpl postService;

    Post basePost;

    @BeforeEach
    void init() {
        basePost = new Post();
        basePost.setId(1L);
    }

    private Post createPost(Long id) {
        Post p = new Post();
        p.setId(id);
        return p;
    }

    // ========================================================================
    // BASIC READ TESTS
    // ========================================================================

    @Test
    void getById_returnPost_whenPostExists() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(basePost));

        Post result = postService.getById(1L);

        assertEquals(1L, result.getId());
        verify(postRepository).findById(1L);
    }

    @Test
    void getById_throwsException_whenPostDoesNotExist() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> postService.getById(1L));

        verify(postRepository).findById(1L);
    }

    @Test
    void mostRecent_postsFromRepository() {
        when(postRepository.findTop10ByOrderByCreatedAtDesc())
                .thenReturn(List.of(createPost(1L), createPost(2L)));

        List<Post> result = postService.mostRecent(10);

        assertEquals(2, result.size());
        verify(postRepository).findTop10ByOrderByCreatedAtDesc();
    }

    @Test
    void topCommented_postsFromRepository() {
        when(postRepository.findTop10MostCommented())
                .thenReturn(List.of(createPost(1L), createPost(2L), createPost(3L)));

        List<Post> result = postService.topCommented(10);

        assertEquals(3, result.size());
        verify(postRepository).findTop10MostCommented();
    }

    @Test
    void findByCreatorId_postsFromRepository() {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        when(postRepository.findByCreatorId(1L, sort))
                .thenReturn(List.of(createPost(1L), createPost(2L)));

        List<Post> result = postService.findByCreatorId(1L, 10);

        assertEquals(2, result.size());
        verify(postRepository).findByCreatorId(1L, sort);
    }

    @Test
    void searchByText_postRepository() {
        when(postRepository.search("text"))
                .thenReturn(List.of(createPost(1L), createPost(2L)));

        List<Post> result = postService.search("text", 10);

        assertEquals(2, result.size());
        verify(postRepository).search("text");
    }

    // ========================================================================
    // CREATE / EDIT
    // ========================================================================

    @Test
    void create_shouldCreatePost() {
        Role role = new Role();
        role.setName(ERole.ROLE_USER);

        User creator = new User();
        creator.setId(1L);
        creator.setBlocked(false);
        creator.setRoles(Set.of(role));

        when(postRepository.save(any(Post.class))).thenReturn(basePost);

        Post created = postService.create(creator, "Hi", "How we doing?");

        assertEquals(1L, created.getId());

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(captor.capture());

        Post saved = captor.getValue();
        assertEquals("Hi", saved.getTitle());
        assertEquals("How we doing?", saved.getContent());
        assertEquals(0, saved.getLikesCount());
        assertEquals(creator, saved.getCreator());
    }

    @Test
    void edit_ShouldUpdate_whenUserIsOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setBlocked(false);

        Post post = new Post();
        post.setId(99L);
        post.setCreator(owner);
        post.setTitle("old");
        post.setContent("old");

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        Post updated = postService.edit(99L, owner, "new", "text");

        assertEquals("new", updated.getTitle());
        assertEquals("text", updated.getContent());
        verify(postRepository).save(post);
    }

    @Test
    void edit_ShouldThrow_whenUserIsNotOwnerOrAdmin() {
        User owner = new User();
        owner.setId(1L);
        owner.setBlocked(false);

        User stranger = new User();
        stranger.setId(2L);
        stranger.setBlocked(false);

        Post post = new Post();
        post.setId(99L);
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        assertThrows(AuthorizationException.class,
                () -> postService.edit(99L, stranger, "x", "y"));
    }

    // ========================================================================
    // DELETE
    // ========================================================================

    @Test
    void delete_ShouldDelete_whenOwner() {
        User owner = new User();
        owner.setId(1L);
        owner.setBlocked(false);

        Post post = createPost(99L);
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        postService.delete(99L, owner);

        verify(postRepository).delete(post);
    }

    @Test
    void delete_ShouldDelete_whenAdmin() {
        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        User admin = new User();
        admin.setId(1L);
        admin.setBlocked(false);
        admin.setRoles(Set.of(role));

        User owner = new User();
        owner.setId(123L);

        Post post = createPost(99L);
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        postService.delete(99L, admin);

        verify(postRepository).delete(post);
    }

    @Test
    void delete_ShouldThrow_whenBlocked() {
        User blocked = new User();
        blocked.setBlocked(true);

        User owner = new User();
        owner.setId(1L);

        Post post = createPost(99L);
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        assertThrows(AuthorizationException.class,
                () -> postService.delete(99L, blocked));

        verify(postRepository, never()).delete(any());
    }

    // ========================================================================
    // LIKE / UNLIKE
    // ========================================================================

    @Test
    void like_ShouldIncreaseLikes() {
        User owner = new User();
        owner.setId(1L);
        owner.setBlocked(false);

        User liker = new User();
        liker.setId(2L);
        liker.setBlocked(false);

        Post post = createPost(99L);
        post.setCreator(owner);
        post.setLikesCount(0);
        post.setLikedBy(new HashSet<>());

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        postService.like(99L, liker);

        verify(postRepository).save(post);
        assertEquals(1, post.getLikesCount());
        assertTrue(post.getLikedBy().contains(liker));
    }

    @Test
    void like_ShouldThrow_whenLikingOwnPost() {
        User user = new User();
        user.setId(1L);
        user.setBlocked(false);

        Post post = createPost(99L);
        post.setCreator(user);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        assertThrows(AuthorizationException.class,
                () -> postService.like(99L, user));
    }

    @Test
    void unlike_ShouldDecreaseLikes() {
        User owner = new User();
        owner.setId(1L);
        owner.setBlocked(false);

        User liker = new User();
        liker.setId(2L);
        liker.setBlocked(false);

        Post post = createPost(99L);
        post.setCreator(owner);
        post.setLikesCount(1);
        post.setLikedBy(new HashSet<>(Set.of(liker)));

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        postService.unlike(99L, liker);

        verify(postRepository).save(post);
        assertEquals(0, post.getLikesCount());
        assertFalse(post.getLikedBy().contains(liker));
    }

    // ========================================================================
    // TAGS — correct version
    // ========================================================================

    @Test
    void addTags_ShouldAddNewTags() {
        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        User creator = new User();
        creator.setId(1L);
        creator.setBlocked(false);
        creator.setRoles(Set.of(role));

        Post post = createPost(99L);
        post.setCreator(creator);
        post.setTags(new HashSet<>());

        Tag tag = new Tag();
        tag.setId(10L);
        tag.setName("name");

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));
        when(tagService.findOrCreate("name")).thenReturn(tag);

        postService.addTagsToPost(99L, creator, List.of("name"));

        assertTrue(post.getTags().contains(tag));
        verify(postRepository).save(post);
    }

    @Test
    void removeTag_ShouldRemoveTag() {
        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        User user = new User();
        user.setId(1L);
        user.setBlocked(false);
        user.setRoles(Set.of(role));

        Tag tag = new Tag();
        tag.setId(10L);
        tag.setName("name");

        Post post = createPost(99L);
        post.setCreator(user);
        post.setTags(new HashSet<>(Set.of(tag)));

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));
        when(tagService.getByName("name")).thenReturn(tag);

        postService.removeTagFromPost(99L, user, "name");

        assertFalse(post.getTags().contains(tag));
        verify(postRepository).save(post);
    }

    @Test
    void removeTag_ShouldThrow_WhenUserBlocked() {
        User owner = new User();
        owner.setId(1L);
        owner.setBlocked(false);

        User blocked = new User();
        blocked.setId(2L);
        blocked.setBlocked(true);

        Post post = createPost(99L);
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        assertThrows(AuthorizationException.class,
                () -> postService.removeTagFromPost(99L, blocked, "name"));

        verify(postRepository, never()).save(any());
    }
}