package org.example.forumstartup.services;


import org.example.forumstartup.enums.ERole;
import org.example.forumstartup.exceptions.AuthorizationException;
import org.example.forumstartup.exceptions.EntityNotFoundException;
import org.example.forumstartup.models.Post;
import org.example.forumstartup.models.Role;
import org.example.forumstartup.models.User;
import org.example.forumstartup.repositories.PostRepository;
import org.example.forumstartup.repositories.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTests {
    @Mock
    private PostRepository postRepository;
    @Mock
    private TagRepository tagRepository;
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

    @Test
    void getById_returnPost_whenPostExists() {

        //Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.of(basePost));
        //Act
        Post result = postService.getById(1L);

        //Assert
        assertEquals(1L, result.getId());
        verify(postRepository).findById(1L);
    }

    @Test
    void getById_throwsException_whenPostDoesNotExist() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EntityNotFoundException.class,
                () -> postService.getById(1L));

        verify(postRepository).findById(1L);
    }

    @Test
    void mostRecent_postsFormRepository() {
        //Arrange
        createPost(1L);
        createPost(2L);
        when(postRepository.findTop10ByOrderByCreatedAtDesc()).
                thenReturn(List.of(createPost(1L), createPost(2L)));
        //Act
        List<Post> result = postService.mostRecent(10);

        //Assert
        assertEquals(2, result.size());
        verify(postRepository).findTop10ByOrderByCreatedAtDesc();

    }

    @Test
    void topCommented_postsFromRepository() {

        //Arrange
        createPost(1L);
        createPost(2L);
        createPost(3L);

        when(postRepository.findTop10MostCommented()).
                thenReturn(List.of(createPost(1L), createPost(2L), createPost(3L)));

        //Act

        List<Post> result = postService.topCommented(10);

        //Assert
        assertEquals(3, result.size());
        verify(postRepository).findTop10MostCommented();
    }

    @Test
    void findByCreatorId_postsFromRepository() {

        //Arrange
        User creator = new User();
        creator.setId(1L);
        createPost(1L);
        createPost(2L);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

        when(postRepository.findByCreatorId(creator.getId(), sort)).
                thenReturn(List.of(createPost(1L), createPost(2L)));

        //Act
        List<Post> result = postService.findByCreatorId(1L, 10);

        //Assert
        assertEquals(2, result.size());
        verify(postRepository).
                findByCreatorId(creator.getId(), sort);
    }

    @Test
    void searchByText_postRepository() {

        //Arrange
        String text = "text";
        createPost(1L);
        createPost(2L);
        when(postRepository.search(text)).
                thenReturn(List.of(createPost(1L), createPost(2L)));

        //Act
        List<Post> result = postService.search(text, 10);

        //Assert
        assertEquals(2, result.size());
        verify(postRepository).search(text);
    }

    @Test
    void create_shouldCreatePost() {

        //Arrange
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        User creator = new User();
        creator.setId(1L);
        creator.setBlocked(false);
        creator.setRoles(Set.of(role));
        String title = "Hi";
        String content = "How we doing'?";

        when(postRepository.save(any(Post.class))).thenReturn(basePost);

        //Act
        Post created = postService.create(creator, title, content);

        //Assert
        assertEquals(1L, created.getId());
        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(argumentCaptor.capture());
        Post captured = argumentCaptor.getValue();
        assertEquals(creator, captured.getCreator());
        assertEquals("Hi", captured.getTitle());
        assertEquals("How we doing'?", captured.getContent());
        assertEquals(0, captured.getLikesCount());
    }

    @Test
    void edit_ShouldUpdate_whenUserIsOwner() {

        //Arrange
        User owner = new User();
        owner.setId(1L);
        owner.setBlocked(false);
        Post post = new Post();
        post.setId(99L);
        post.setCreator(owner);
        post.setContent("old text");
        post.setTitle("old title");

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        //Act
        Post updated = postService.edit(99L, owner, "new title", "new text");

        //Assert
        assertEquals(owner, updated.getCreator());
        assertEquals("new title", updated.getTitle());
        assertEquals("new text", updated.getContent());

        ArgumentCaptor<Post> argumentCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(argumentCaptor.capture());
        Post saved = argumentCaptor.getValue();
        assertEquals("new title", saved.getTitle());
        assertEquals("new text", saved.getContent());
    }

    @Test
    void edit_ShouldThrow_whenUserIsNotOwnerAndNotAdmin() {
        //Arrange
        User owner = new User();
        User notOwner = new User();
        owner.setId(1L);
        owner.setBlocked(false);
        notOwner.setId(2L);
        notOwner.setBlocked(false);
        Post post = new Post();
        post.setId(99L);
        post.setContent("old text");
        post.setTitle("old title");
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        //Assert
        assertThrows(AuthorizationException.class,
                () -> postService.edit(99L, notOwner, "old title", "old text"));
        verify(postRepository).findById(99L);
    }

    @Test
    void delete_ShouldDelete_whenUserIsOwner() {

        //Arrange
        Role role = new Role();
        role.setName(ERole.ROLE_USER);
        User creator = new User();
        creator.setId(1L);
        creator.setBlocked(false);
        creator.setRoles(Set.of(role));

        Post post = createPost(99L);
        post.setCreator(creator);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        //Act
        postService.delete(99L, creator);

        //Assert
        verify(postRepository).findById(99L);
        verify(postRepository).delete(post);
    }

    @Test
    void delete_ShouldDelete_whenUserIsAdmin() {

        //Arrange
        Role role = new Role();
        role.setName(ERole.ROLE_ADMIN);
        User admin = new User();
        admin.setId(1L);
        admin.setBlocked(false);
        admin.setRoles(Set.of(role));
        User creator = new User();
        creator.setId(123L);
        Post post = createPost(99L);
        post.setCreator(creator);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        //Act
        postService.delete(99L, admin);

        //Assert
        verify(postRepository).findById(99L);
        verify(postRepository).delete(post);
    }

    @Test
    void delete_ShouldThrow_whenUserIsNotOwnerOrAdmin() {
        //Arrange
        User owner = new User();
        User notOwner = new User();
        owner.setId(1L);
        owner.setBlocked(false);
        notOwner.setId(2L);
        notOwner.setBlocked(false);
        Post post = new Post();
        post.setId(99L);
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        assertThrows(AuthorizationException.class,
                () -> postService.delete(99L, notOwner));
        verify(postRepository).findById(99L);
        verify(postRepository, never()).delete(any());
    }

    @Test
    void delete_ShouldThrow_whenUserIsBlocked() {
        //Arrange
        User owner = new User();
        User blocked = new User();
        owner.setId(1L);
        owner.setBlocked(false);
        blocked.setId(2L);
        blocked.setBlocked(true);
        Post post = new Post();
        post.setId(99L);
        post.setCreator(owner);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        assertThrows(AuthorizationException.class,
                () -> postService.delete(99L, blocked));
        verify(postRepository).findById(99L);
        verify(postRepository, never()).delete(any());
    }

    @Test
    void adminDelete_ShouldDelete_whenAdmin() {

        // Arrange
        Role adminRole = new Role();
        adminRole.setName(ERole.ROLE_ADMIN);

        User admin = new User();
        admin.setId(99L);
        admin.setBlocked(false);
        admin.setRoles(Set.of(adminRole));

        Post post = createPost(99L);

        when(postRepository.findById(99L)).thenReturn(Optional.of(post));

        // Act
        postService.adminDelete(99L, admin);

        // Assert
        verify(postRepository).findById(99L);
        verify(postRepository).delete(post);
    }

    @Test
    void adminDelete_ShouldThrow_whenNotAdmin() {

            // Arrange
            User notAdmin = new User();
            notAdmin.setId(2L);
            notAdmin.setBlocked(false);
            notAdmin.setRoles(Set.of());

            // Act + Assert
            assertThrows(AuthorizationException.class,
                    () -> postService.adminDelete(99L, notAdmin));

            verify(postRepository, never()).findById(any());
            verify(postRepository, never()).delete(any());
        }
    }

